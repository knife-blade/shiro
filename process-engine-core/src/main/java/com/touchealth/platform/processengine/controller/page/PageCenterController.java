package com.touchealth.platform.processengine.controller.page;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.PermsConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.*;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.entity.user.UserPerms;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.handler.PageCenterHelper;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.channel.PlatformVersionDetailsDto;
import com.touchealth.platform.processengine.pojo.dto.channel.PlatformVersionDto;
import com.touchealth.platform.processengine.pojo.dto.page.*;
import com.touchealth.platform.processengine.pojo.query.PageManagerQuery;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.page.*;
import com.touchealth.platform.processengine.service.user.UserPermsService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.SC_ADMIN_ID;
import static com.touchealth.platform.processengine.constant.CommonConstant.STATUS.TRASH;
import static com.touchealth.platform.processengine.constant.PageCenterConsts.CombinationType.TYPE_COMPONENT_A;

/**
 * 页面中心
 *
 * @author liufengqiang
 * @date 2020-11-16 19:37:41
 */
@RestController
@RequestMapping("/page-center")
@Slf4j
public class PageCenterController {

    @Resource
    private PlatformVersionService platformVersionService;
    @Resource
    private PageOperationLogService pageOperationLogService;
    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private UserService userService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private UserPermsService userPermsService;
    @Resource
    private PlatformReleaseMsgService platformReleaseMsgService;
    @Resource
    private PlatformChannelService platformChannelService;

    @Value("${oss.aliyun.end.point}")
    private String ossPoint;
    @Value("${oss.aliyun.prefix}")
    private String ossPrefix;

    /**
     * 版本列表
     */
    @GetMapping("/version")
    public Response versionList(@RequestHeader String channelNo) {
        List<PlatformVersion> platformVersions = platformVersionService.listByChannelNo(channelNo);
        if (CollectionUtils.isEmpty(platformVersions)) {
            return Response.ok(BaseHelper.r2t(platformVersionService.createNewVersion(channelNo, null), PlatformVersionDto.class));
        }
        return Response.ok(BaseHelper.r2t(platformVersions, PlatformVersionDto.class));
    }

    /**
     * 版本操作历史
     */
    @GetMapping("/version/history")
    public Response version(@RequestHeader String channelNo, Long versionId) {
        PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);

        // 页面列表
        PageManagerQuery query = new PageManagerQuery();
        query.setVersionId(platformVersion.getId());
        query.setIsFolder(false);
        query.setOrderBy(1);
        query.setIsDeleted(null);
        List<PageManager> pageManagers = pageManagerService.listByParam(query).stream().filter(o -> o.getChangeStatus() != 2).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(pageManagers)) {
            return Response.success;
        }

        // 上个版本
        Long lastVersionId = null;
        try {
            int lastVersionNo = Integer.parseInt(platformVersion.getVersionName().substring(2));
            if (lastVersionNo > 1) {
                PlatformVersion lastVersion = platformVersionService.getByVersionName(channelNo, "版本" + (lastVersionNo - 1));
                if (lastVersion != null) {
                    lastVersionId = lastVersion.getId();
                }
            }
        } catch (Exception e) {
            throw new BusinessException("版本号解析异常", e);
        }

        // 操作人ids
        Set<Long> operatorIds = new HashSet<>();
        pageManagers.forEach(o -> {
            if (StringUtils.isNotBlank(o.getOperatorIds())) {
                operatorIds.addAll(Arrays.stream(o.getOperatorIds().split(",")).map(Long::parseLong).collect(Collectors.toList()));
            }
        });

        // 操作人列表
        List<User> users;
        if (CollectionUtils.isNotEmpty(operatorIds)) {
            users = userService.listByIds(operatorIds);
        } else {
            users = new ArrayList<>();
        }
        Map<Long, String> userMap = users.stream().filter(o -> StringUtils.isNotBlank(o.getAvatar()))
                .collect(Collectors.toMap(User::getId, User::getAvatar));

        // 文件夹
        Set<Long> folderIds = pageManagers.stream().filter(o -> o.getFolderId() != null).map(PageManager::getFolderId).collect(Collectors.toSet());
        List<PageManager> folder;
        if (CollectionUtils.isNotEmpty(folderIds)) {
            folder = pageManagerService.listByIds(folderIds);
        } else {
            folder = new ArrayList<>();
        }
        Map<Long, String> folderMap = folder.stream().collect(Collectors.toMap(PageManager::getId, PageManager::getPageName));

        // 构造返回值
        List<PlatformVersionDetailsDto.VersionDto> savePages = new ArrayList<>();
        List<PlatformVersionDetailsDto.VersionDto> updatePages = new ArrayList<>();
        List<PlatformVersionDetailsDto.VersionDto> deletePages = new ArrayList<>();
        for (PageManager pageManager : pageManagers) {
            PlatformVersionDetailsDto.VersionDto dto = new PlatformVersionDetailsDto.VersionDto();
            dto.setPageName(pageManager.getPageName());
            if (StringUtils.isNotBlank(pageManager.getOperatorIds())) {
                List<String> avatars = new ArrayList<>();
                Arrays.asList(pageManager.getOperatorIds().split(",")).forEach(x -> {
                    String avatar = userMap.get(Long.parseLong(x));
                    if (StringUtils.isNotBlank(avatar)) {
                        avatars.add(avatar);
                    }
                });
                dto.setAvatars(avatars);
            }

            if (pageManager.getChangeStatus() == PageCenterConsts.ChangerStatus.ADD.ordinal() || pageManager.getChangeStatus() == PageCenterConsts.ChangerStatus.ALERT.ordinal()) {
                if (pageManager.getChangeStatus() == PageCenterConsts.ChangerStatus.ADD.ordinal()) {
                    if (!pageManager.getStatus().equals(TRASH.getCode())) {
                        savePages.add(dto);
                    }
                } else {
                    if (lastVersionId != null) {
                        List<PageModule> newPageModules = pageModuleService.listByPageId(pageManager.getId());
                        Map<Long, PageModule> newPageModuleMap = newPageModules.stream().collect(Collectors.toMap(PageModule::getModuleUniqueId, x -> x, (x, y) -> y));

                        PageManager oldPageManager = pageManagerService.getByPageUniqueId(lastVersionId, pageManager.getPageUniqueId());
                        List<PageModule> oldPageModules = pageModuleService.listByPageId(oldPageManager.getId());
                        Map<Long, PageModule> oldPageModuleMap = oldPageModules.stream().collect(Collectors.toMap(PageModule::getModuleUniqueId, x -> x, (x, y) -> y));

                        List<VersionOperationLogDto> operationLogs = new ArrayList<>();
                        newPageModules.forEach(x -> {
                            PageModule oldPageModule = oldPageModuleMap.get(x.getModuleUniqueId());
                            List<CompareBo> compare = moduleService.getInstance(x.getModuleType()).compare(x.getWebJson(), oldPageModule == null ? null : oldPageModule.getModuleId());
                            getLogDto(compare, folderMap, pageManager, x, operationLogs);
                        });

                        oldPageModules.forEach(x -> {
                            PageModule pageModule = newPageModuleMap.get(x.getModuleUniqueId());
                            if (pageModule == null) {
                                // 删除组件
                                List<CompareBo> compare = moduleService.getInstance(x.getModuleType()).compare(null, x.getModuleId());
                                getLogDto(compare, folderMap, pageManager, x, operationLogs);
                            }
                        });

                        dto.setOperationLogs(operationLogs);
                    }
                    updatePages.add(dto);
                }
            } else if (pageManager.getChangeStatus() == PageCenterConsts.ChangerStatus.DELETE.ordinal() && pageManager.getOldChangeStatus() != PageCenterConsts.ChangerStatus.ADD.ordinal()) {
                deletePages.add(dto);
            }
        }

        PlatformVersionDetailsDto dto = new PlatformVersionDetailsDto();
        dto.setUpdateNum(savePages.size() + updatePages.size() + deletePages.size());
        dto.setAddPages(savePages);
        dto.setRealNames(users.stream().map(User::getRealName).collect(Collectors.toList()));
        dto.setUpdatePages(updatePages);
        dto.setDeletePages(deletePages);
        return Response.ok(dto);
    }

    private void getLogDto(List<CompareBo> compare, Map<Long, String> folderMap, PageManager pageManager, PageModule x, List<VersionOperationLogDto> operationLogs) {
        if (CollectionUtils.isEmpty(compare)) {
            return;
        }

        List<VersionOperationLogDto> logDtos = compare.stream().map(o -> {
            PageOperationLog operationLog = new PageOperationLog();
            operationLog.setCombinationType(TYPE_COMPONENT_A.getCode());
            operationLog.setOperate(o.getModifyAction());
            operationLog.setFolderName(folderMap.get(pageManager.getFolderId()));
            operationLog.setPageName(pageManager.getPageName());
            operationLog.setModuleName(o.getModuleType().getName() + "组件");
            operationLog.setContent(o.getModifyName());
            operationLog.setName(o.getModifyActionContent());
            operationLog.setInitValue(o.getModifyBeforeValue());
            operationLog.setFinishValue(o.getModifyAfterValue());

            VersionOperationLogDto logDto = PageCenterHelper.getLogContent(operationLog, ossPoint + File.separator + ossPrefix);
            List<PageOperationLog> pageOperationLogs = pageOperationLogService.listByModuleIdAndName(x.getId(), o.getModifyActionContent());
            if (CollectionUtils.isNotEmpty(pageOperationLogs)) {
                List<User> users1 = userService.listByIds(pageOperationLogs.stream().map(PageOperationLog::getUpdatedBy).collect(Collectors.toSet()));
                logDto.setAvatars(users1.stream().map(User::getAvatar).collect(Collectors.toList()));
            }
            return logDto;
        }).collect(Collectors.toList());
        operationLogs.addAll(logDtos);
    }

    /**
     * 回收站
     */
    @GetMapping("/recycle-bin")
    public Response recycleBin(@RequestHeader String channelNo) {
        List<PageManager> pageManagers = pageManagerService.listRecycleBinByChannelNo(channelNo);

        Map<Long, List<PageManagerDto>> childrenMap = new HashMap<>(16);
        pageManagers.forEach(o -> {
            if (o.getFolderId() != null) {
                List<PageManagerDto> children = childrenMap.get(o.getFolderId());
                if (children == null) {
                    children = new ArrayList<>();
                }
                children.add(BaseHelper.r2t(o, PageManagerDto.class));
                childrenMap.put(o.getFolderId(), children);
            }
        });

        Set<Long> folderIds = pageManagers.stream().filter(o -> o.getFolderId() != null).map(PageManager::getFolderId).collect(Collectors.toSet());
        List<PageManager> folderPages;
        if (CollectionUtils.isNotEmpty(folderIds)) {
            folderPages = pageManagerService.listByIds(folderIds);
        } else {
            folderPages = new ArrayList<>();
        }
        Map<Long, Boolean> folderMap = folderPages.stream().collect(Collectors.toMap(PageManager::getId, o -> TRASH.getCode().equals(o.getStatus()), (x, y) -> y));

        Map<Long, List<PageManagerDto>> pageListMap = new LinkedHashMap<>(16);
        pageManagers.forEach(o -> {
            // 文件夹id为空的 || 文件夹id没被删的
            if (o.getFolderId() == null || (o.getFolderId() != null && !folderMap.get(o.getFolderId()))) {
                PageManagerDto dto = BaseHelper.r2t(o, PageManagerDto.class);
                dto.setChildren(childrenMap.get(o.getId()));

                List<PageManagerDto> pageListDtos = pageListMap.get(o.getVersionId());
                if (pageListDtos == null) {
                    pageListDtos = new ArrayList<>();
                }
                pageListDtos.add(dto);
                pageListMap.put(o.getVersionId(), pageListDtos);
            }
        });

        Map<Long, String> versionMap;
        if (CollectionUtils.isNotEmpty(pageManagers)) {
            List<PlatformVersion> platformVersions = platformVersionService.listByIds(pageManagers.stream().map(PageManager::getVersionId).collect(Collectors.toSet()));
            versionMap = platformVersions.stream().collect(Collectors.toMap(PlatformVersion::getId, PlatformVersion::getVersionName, (x, y) -> y));
        } else {
            versionMap = new HashMap<>(16);
        }

        List<RecycleBinDto> recycleBinDtos = new ArrayList<>();
        pageListMap.forEach((k, v) -> {
            RecycleBinDto pageManagerDto = new RecycleBinDto();
            pageManagerDto.setVersionId(k);
            pageManagerDto.setVersionName(versionMap.get(k));
            pageManagerDto.setPageList(v);
            recycleBinDtos.add(pageManagerDto);
        });
        return Response.ok(recycleBinDtos);
    }

    /**
     * 版本发布
     */
    @PostMapping("/release")
    public Response release(@RequestHeader String channelNo, @RequestAttribute Long userId, @RequestParam Long versionId) {
        PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);
        Assert.isTrue(!PageCenterConsts.VersionStatus.RELEASE.getCode().equals(platformVersion.getStatus()) &&
                versionId.equals(platformVersion.getId()), "当前版本已发布，请刷新页面");

        PlatformVersion newVersion = platformVersionService.release(userId, platformVersion);
        return Response.ok(BaseHelper.r2t(newVersion, PlatformVersionDto.class));
    }

    /**
     * 提交审批：没有发布权限的时候点发布以消息通知的形式发送至有发布权限的人
     *
     * @param isApproval 是否审批 true或null为提交审批 false为取消提交审批
     */
    @PostMapping("/approval")
    public Response approval(@RequestHeader String channelNo, @RequestAttribute Long userId, @RequestParam Long versionId, Boolean isApproval) {
        PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);
        Assert.isTrue(versionId.equals(platformVersion.getId()), "当前版本已发布，请刷新页面");

        Integer status;
        if (Boolean.FALSE.equals(isApproval)) {
            Assert.isTrue(PageCenterConsts.VersionStatus.LOCK.getCode().equals(platformVersion.getStatus()), "版本还未提交审批，不能取消");
            status = PageCenterConsts.VersionStatus.DRAFT.getCode();
        } else {
            Assert.isTrue(PageCenterConsts.VersionStatus.DRAFT.getCode().equals(platformVersion.getStatus()), "当前版本不是草稿版本，不能提交审批");
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
            Assert.notNull(platformChannel, "渠道不存在");

            List<UserPerms> userPerms = userPermsService.listByPermsCode(PermsConstant.OP_PAGE_RELEASE, channelNo);
            Set<Long> userIds = userPerms.stream().map(UserPerms::getUserId).collect(Collectors.toSet());
            userIds.addAll(Arrays.asList(SC_ADMIN_ID, platformChannel.getAdminId()));

            User user = userService.getById(userId);
            String msg = user.getRealName() + "向您提交" + platformVersion.getVersionName() + "的发布请求，请及时确认内容并发布。";

            platformReleaseMsgService.saveMsg(userIds.stream().map(o -> {
                PlatformReleaseMsg releaseMsg = new PlatformReleaseMsg();
                releaseMsg.setUserId(o);
                releaseMsg.setVersionId(versionId);
                releaseMsg.setMessage(msg);
                return releaseMsg;
            }).collect(Collectors.toList()));
            status = PageCenterConsts.VersionStatus.LOCK.getCode();
        }

        // 更新版本状态为锁定状态
        platformVersionService.update(new PlatformVersion(), Wrappers.<PlatformVersion>lambdaUpdate()
                .set(PlatformVersion::getStatus, status)
                .set(PlatformVersion::getAuthUserId, userId)
                .eq(PlatformVersion::getId, versionId));
        return Response.success;
    }

    /**
     * 操作日志
     */
    @GetMapping("/operation-log")
    public Response operationLog(@RequestHeader String channelNo, Long versionId) {
        return Response.ok(platformVersionService.operationLog(channelNo, versionId));
    }

    /**
     * 发布提交审批消息
     */
    @GetMapping("/release-msg")
    public Response releaseMsg(@RequestAttribute Long userId) {
        try {
            // 任何情况下这个接口都不应该报错
            List<PlatformReleaseMsg> releaseMessages = platformReleaseMsgService.list(Wrappers.<PlatformReleaseMsg>lambdaQuery().eq(PlatformReleaseMsg::getUserId, userId));
            List<String> messages = releaseMessages.stream().map(PlatformReleaseMsg::getMessage).collect(Collectors.toList());
            platformReleaseMsgService.removeByIds(releaseMessages.stream().map(PlatformReleaseMsg::getId).collect(Collectors.toList()));
            return Response.ok(messages);
        } catch (Exception e) {
            log.error("发布提交审批消息异常", e);
            return Response.success;
        }
    }
}
