package com.touchealth.platform.processengine.service.impl.page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.dao.page.PlatformVersionDao;
import com.touchealth.platform.processengine.entity.page.*;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.handler.PageCenterHelper;
import com.touchealth.platform.processengine.pojo.dto.page.OperationLogDto;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.module.common.HomeNavService;
import com.touchealth.platform.processengine.service.page.*;
import com.touchealth.platform.processengine.service.user.UserService;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.RedisConstant.PageCenter.getPageLockKeyByUserId;

/**
 * 版本号只允许递增
 *
 * @author liufengqiang
 * @date 2020-11-17 11:39:13
 */
@Service
public class PlatformVersionServiceImpl extends BaseServiceImpl<PlatformVersionDao, PlatformVersion> implements PlatformVersionService {

    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private RedisService redisService;
    @Resource
    private PageOperationLogService pageOperationLogService;
    @Resource
    private HomeNavService homeNavService;
    @Resource
    private UserService userService;
    @Resource
    private PlatformReleaseMsgService releaseMessageService;

    @Value("${oss.aliyun.end.point}")
    private String ossPoint;
    @Value("${oss.aliyun.prefix}")
    private String ossPrefix;

    @Override
    public List<PlatformVersion> listByChannelNo(String channelNo) {
        return list(new QueryWrapper<PlatformVersion>().lambda()
                .eq(PlatformVersion::getChannelNo, channelNo)
                .orderByDesc(PlatformVersion::getCreatedTime));
    }

    @Override
    @TransactionalForException
    public PlatformVersion release(Long userId, PlatformVersion platformVersion) {
        // 更新当前版本状态：已发布
        update(new PlatformVersion(), Wrappers.<PlatformVersion>lambdaUpdate()
                .set(PlatformVersion::getStatus, CommonConstant.STATUS.PUBLISHED.getCode())
                .eq(PlatformVersion::getId, platformVersion.getId()));

        // 创建新的草稿版本
        PlatformVersion newVersion = createNewVersion(platformVersion.getChannelNo(), platformVersion.getVersionName());

        // 复制上个版本页面归属新版本
        List<PageManager> oldPageManagers = pageManagerService.listByVersionId(platformVersion.getId(), false, null);
        pageManagerService.copyPage(oldPageManagers, newVersion);

        //复制首页导航组件
        homeNavService.copyHomeNav(platformVersion, newVersion);

        // 更新平台生产版本号和页面版本号
        platformChannelService.update(new PlatformChannel(), Wrappers.<PlatformChannel>lambdaUpdate()
                .set(PlatformChannel::getReleaseVersion, platformVersion.getId())
                .eq(PlatformChannel::getChannelNo, platformVersion.getChannelNo()));

        // 清除线上版本锁
        redisService.del(getPageLockKeyByUserId(userId));

        // 增加版本发布日志
        pageOperationLogService.saveReleaseLog(userId, platformVersion, newVersion.getVersionName());

        // 删除审批消息
        releaseMessageService.remove(Wrappers.<PlatformReleaseMsg>lambdaQuery().eq(PlatformReleaseMsg::getVersionId, platformVersion.getId()));
        return newVersion;
    }

    @Override
    public PlatformVersion getDefaultVersion(String channelNo, Long versionId) {
        PlatformVersion platformVersion;
        if (versionId == null) {
            // 最新的非发布版本
            platformVersion = getOne(new QueryWrapper<PlatformVersion>().lambda()
                    .eq(PlatformVersion::getChannelNo, channelNo)
                    .ne(PlatformVersion::getStatus, PageCenterConsts.VersionStatus.RELEASE.getCode())
                    .last("limit 1")
                    .orderByDesc(PlatformVersion::getCreatedTime));
        } else {
            platformVersion = getById(versionId);
        }
        Assert.notNull(platformVersion, "版本不存在");
        return platformVersion;
    }

    @Override
    public Long getDefaultVersionId(String channelNo, Long versionId) {
        return versionId == null ? getDefaultVersion(channelNo, null).getId() : versionId;
    }

    @Override
    public PlatformVersion getByVersionName(String channelNo, String versionName) {
        return getOne(Wrappers.<PlatformVersion>lambdaQuery().eq(PlatformVersion::getChannelNo, channelNo).eq(PlatformVersion::getVersionName, versionName));
    }

    @Override
    public void updateVersionTime(Long versionId) {
        update(Wrappers.<PlatformVersion>lambdaUpdate().set(PlatformVersion::getUpdatedTime, LocalDateTime.now()).eq(PlatformVersion::getId, versionId));
    }

    @Override
    public OperationLogDto operationLog(String channelNo, Long versionId) {
        PlatformVersion platformVersion = getDefaultVersion(channelNo, versionId);
        OperationLogDto operationLogDto = new OperationLogDto();
        operationLogDto.setVersionName(platformVersion.getVersionName());

        List<PageOperationLog> pageOperationLogs = pageOperationLogService.listByVersionId(platformVersion.getId());
        Map<String, List<PageOperationLog>> logMap = new LinkedHashMap<>();
        pageOperationLogs.forEach(o -> {
            List<PageOperationLog> logs = logMap.get(o.getOperationNo());
            if (logs == null) {
                logs = new ArrayList<>();
            }
            logs.add(o);
            logMap.put(o.getOperationNo(), logs);
        });

        List<OperationLogDto.LogDto> logs = new ArrayList<>();
        logMap.forEach((k, v) -> {
            PageOperationLog pageOperationLog = v.get(0);
            OperationLogDto.LogDto dto = new OperationLogDto.LogDto();
            dto.setCreatedTime(pageOperationLog.getCreatedTime());
            dto.setUpdatedPage(pageOperationLog.getPageName());

            if (pageOperationLog.getUpdatedBy() != null) {
                User user = userService.getById(pageOperationLog.getUpdatedBy());
                if (user != null) {
                    dto.setAvatar(user.getAvatar());
                    dto.setRealName(user.getRealName());
                }
            }

            dto.setUpdatedContents(v.stream().map(o -> PageCenterHelper.getLogContent(o, ossPoint + File.separator + ossPrefix))
                    .collect(Collectors.toList()));
            logs.add(dto);
        });
        operationLogDto.setLogs(logs);
        return operationLogDto;
    }

    @Override
    @TransactionalForException
    public PlatformVersion createNewVersion(String channelNo, String lastVersionName) {
        PlatformVersion platformVersion = new PlatformVersion();
        platformVersion.setChannelNo(channelNo);
        String versionName;
        if (StringUtils.isEmpty(lastVersionName)) {
            versionName = "版本1";
        } else {
            int no = Integer.parseInt(lastVersionName.substring(2)) + 1;
            versionName = "版本" + no;
        }
        platformVersion.setVersionName(versionName);
        platformVersion.setStatus(CommonConstant.STATUS.DRAFT.getCode());
        save(platformVersion);
        return platformVersion;
    }

}
