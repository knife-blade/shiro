package com.touchealth.platform.processengine.service.impl.page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.dao.page.PageManagerDao;
import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.page.PageManagerDto;
import com.touchealth.platform.processengine.pojo.query.PageManagerQuery;
import com.touchealth.platform.processengine.pojo.request.page.PageModuleRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.module.BaseModuleService;
import com.touchealth.platform.processengine.service.module.common.HomeNavService;
import com.touchealth.platform.processengine.service.module.common.PageTemplateService;
import com.touchealth.platform.processengine.service.page.*;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.utils.ConvertUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.*;

/**
 * @author liufengqiang
 * @date 2020-11-18 15:34:37
 */
@Service
@Slf4j
public class PageManagerServiceImpl extends BaseServiceImpl<PageManagerDao, PageManager> implements PageManagerService {

    @Resource
    private PageOperationLogService pageOperationLogService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private PageManagerDao pageManagerDao;
    @Resource
    private PlatformVersionService platformVersionService;
    @Resource
    private PageTemplateService pageTemplateService;
    @Resource
    private HomeNavService homeNavService;
    @Resource
    private PlatformChannelService platformChannelService;

    @Override
    @TransactionalForException
    public void savePage(Long userId, PageManager pageManager) {
        initPage(userId, pageManager);
        save(pageManager);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageManager.getVersionId());
    }

    @Override
    public void savePage(Long userId, Collection<PageManager> pageManagers) {
        if (CollectionUtils.isNotEmpty(pageManagers)) {
            PageManager loginPageManager = null;
            for (PageManager o : pageManagers) {
                initPage(userId, o);
                if (o.getIsSign() != null && o.getIsSign()) {
                    loginPageManager = o;
                }
            }
            saveBatch(pageManagers);
            // 添加登录模块信息
            if (null != loginPageManager) {
                PageModuleRequest pageModuleRequest = new PageModuleRequest(CommonConstant.ModuleType.LOGIN.getCode());
                pageModuleService.saveModule(userId, loginPageManager, pageModuleRequest);
            }
        }
    }

    private void initPage(Long userId, PageManager pageManager) {
        pageManager.setId(IdWorker.getId(pageManager));
        pageManager.setPageUniqueId(ConvertUtils.encode62(pageManager.getId()));
        pageManager.setSortNo(System.currentTimeMillis());
        pageManager.setStatus(CommonConstant.STATUS.DRAFT.getCode());
        if (pageManager.getBusinessType() == null) {
            pageManager.setBusinessType(PageCenterConsts.BusinessType.COMMON.getCode());
        }

        boolean isSign = pageManager.getIsSign() != null && pageManager.getIsSign();
        List<String> tags = new ArrayList<>(4);
        tags.add(PAGE_TAG_NEW);
        if (!isSign) {
            tags.add(PAGE_TAG_EMPTY);
        }
        if (PageCenterConsts.BusinessType.COMMON.getCode().equals(pageManager.getBusinessType())) {
            if (isSign) {
                pageManager.setRouterName(ROUTER_NAME_LOGIN);
            } else {
                if (StringUtils.isBlank(pageManager.getRouterName())) {
                    pageManager.setRouterName(ROUTER_NAME_ENTRY);
                }
            }
        } else {
            tags.add(PAGE_TAG_BUSINESS);
        }
        pageManager.setPageTags(String.join(",", tags));
        pageOperationLogService.savePageLog(userId, PageCenterConsts.LogOperate.ADD, null, pageManager);
    }

    @Override
    public List<PageManager> listByVersionId(Long versionId, Boolean isDeleted, Collection<Integer> businessType) {
        LambdaQueryWrapper<PageManager> queryWrapper = new QueryWrapper<PageManager>().lambda();
        if (isDeleted != null) {
            if (isDeleted) {
                queryWrapper.eq(PageManager::getStatus, CommonConstant.STATUS.TRASH.getCode());
            } else {
                queryWrapper.ne(PageManager::getStatus, CommonConstant.STATUS.TRASH.getCode());
            }
        }
        return list(queryWrapper.eq(PageManager::getVersionId, versionId)
                .in(CollectionUtils.isNotEmpty(businessType), PageManager::getBusinessType, businessType)
                .orderByDesc(PageManager::getSortNo));
    }

    @Override
    public void updateFolder(PageManager pageManager, Long folderId) {
        update(new PageManager(), Wrappers.<PageManager>lambdaUpdate()
                .set(PageManager::getFolderId, folderId)
                .eq(PageManager::getId, pageManager.getId()));
    }

    @Override
    public List<PageManager> listByBusinessTypes(Long versionId, Collection<Integer> businessTypes) {
        return list(Wrappers.<PageManager>lambdaQuery().eq(PageManager::getVersionId, versionId).in(PageManager::getBusinessType, businessTypes));
    }

    @Override
    public String getFolderNameById(PageManager pageManager) {
        if (pageManager.getFolderId() != null) {
            PageManager folder = getById(pageManager.getFolderId());
            if (folder != null) {
                return folder.getPageName();
            }
        }
        return null;
    }

    @Override
    public Map<Long, String> getFolderMap(Collection<PageManager> pageManagers) {
        Set<Long> pageIds = pageManagers.stream().filter(o -> o.getFolderId() != null).map(PageManager::getFolderId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(pageIds)) {
            List<PageManager> folder = listByIds(pageIds);
            return folder.stream().collect(Collectors.toMap(PageManager::getId, PageManager::getPageName));
        }
        return new HashMap<>(16);
    }

    private String getAlertTag(PageManager pageManager, String tag) {
        Set<String> tagSet;
        if (StringUtils.isNotBlank(pageManager.getPageTags())) {
            tagSet = new HashSet<>(Arrays.asList(pageManager.getPageTags().split(",")));
        } else {
            tagSet = new HashSet<>();
        }
        tagSet.add(tag);
        return String.join(",", tagSet);
    }

    @Override
    public void updateChangeStatus(Long userId, PageManager pageManager) {
        // 更新页面操作人
        String operatorIds = updateOperator(userId, pageManager);

        Set<String> tags;
        if (StringUtils.isNotBlank(pageManager.getPageTags())) {
            tags = new HashSet<>(Arrays.asList(pageManager.getPageTags().split(",")));
        } else {
            tags = new HashSet<>();
        }
        if (pageModuleService.countByPageId(pageManager.getId()) > 0) {
            tags.remove(PAGE_TAG_EMPTY);
        } else {
            tags.add(PAGE_TAG_EMPTY);
        }
        if (pageManager.getChangeStatus() != 0) {
            tags.add(PAGE_TAG_ALERT);
        }

        update(new PageManager(), Wrappers.<PageManager>lambdaUpdate()
                .set(StringUtils.isNotBlank(operatorIds), PageManager::getOperatorIds, operatorIds)
                .set(pageManager.getChangeStatus() != 0, PageManager::getChangeStatus, 1)
                .set(PageManager::getPageTags, String.join(",", tags))
                .eq(PageManager::getId, pageManager.getId()));
    }

    @Override
    public PageManager getByPageUniqueId(Long versionId, String pageUniqueId) {
        return getOne(new QueryWrapper<PageManager>().lambda()
                .eq(PageManager::getVersionId, versionId)
                .eq(PageManager::getPageUniqueId, pageUniqueId));
    }

    /**
     * 更新操作人
     *
     * @param userId
     * @param pageManager
     * @return
     */
    @Override
    public String updateOperator(Long userId, PageManager pageManager) {
        Set<String> operatorIds;
        if (StringUtils.isNotBlank(pageManager.getOperatorIds())) {
            operatorIds = new HashSet<>(Arrays.asList(pageManager.getOperatorIds().split(",")));
            if (operatorIds.contains(String.valueOf(userId))) {
                return null;
            }
        } else {
            operatorIds = new HashSet<>();
        }
        operatorIds.add(String.valueOf(userId));
        pageManager.setOperatorIds(String.join(",", operatorIds));
        return String.join(",", operatorIds);
    }

    @Override
    public List<PageManager> listByParam(PageManagerQuery query) {
        if (query.getModuleType() != null) {
            return pageManagerDao.listByModuleType(query);
        }

        LambdaQueryWrapper<PageManager> queryWrapper = Wrappers.lambdaQuery();
        if (query.getIsDeleted() != null) {
            if (query.getIsDeleted()) {
                queryWrapper.eq(PageManager::getStatus, CommonConstant.STATUS.TRASH.getCode());
            } else {
                queryWrapper.ne(PageManager::getStatus, CommonConstant.STATUS.TRASH.getCode());
            }
        }
        return list(queryWrapper.ne(Integer.valueOf(1).equals(query.getQueryType()), PageManager::getChangeStatus, 2)
                .eq(query.getPageType() != null, PageManager::getPageType, query.getPageType())
                .eq(query.getVersionId() != null, PageManager::getVersionId, query.getVersionId())
                .eq(query.getBusinessType() != null, PageManager::getBusinessType, query.getBusinessType())
                .eq(query.getIsFolder() != null, PageManager::getIsFolder, query.getIsFolder())
                .eq(query.getIsHide() != null, PageManager::getIsHide, query.getIsHide())
                .orderByDesc(Integer.valueOf(1).equals(query.getOrderBy()) ? PageManager::getUpdatedTime : PageManager::getSortNo));
    }

    @Override
    @TransactionalForException
    public void recycleBin(Long userId, Long id) {
        List<PageManager> pageManagers = list(new QueryWrapper<PageManager>().lambda().eq(PageManager::getId, id).or().eq(PageManager::getFolderId, id));
        if (CollectionUtils.isNotEmpty(pageManagers)) {
            List<Long> pageIds = pageManagers.stream().map(PageManager::getId).collect(Collectors.toList());

            // 删除组件
            List<PageModule> pageModules = pageModuleService.listByPageIds(pageIds);
            if (CollectionUtils.isNotEmpty(pageModules)) {
                pageModules.forEach(o -> moduleService.getInstance(o.getModuleType()).updateModuleStatus(o.getModuleId(), CommonConstant.STATUS.TRASH.getCode()));
            }

            // 删除页面
            pageManagers.forEach(o -> {
                o.setStatus(CommonConstant.STATUS.TRASH.getCode());
                if (o.getChangeStatus() == 1 || o.getChangeStatus() == 2) {
                    o.setChangeStatus(3);
                }
                updateOperator(userId, o);
                o.setUpdatedTime(LocalDateTime.now());
            });
            updateBatchById(pageManagers);

            // 增加操作日志
            pageOperationLogService.savePageLog(userId, PageCenterConsts.LogOperate.DELETE, null, pageManagers);

            // 更新草稿版本时间
            platformVersionService.updateVersionTime(pageManagers.get(0).getVersionId());
        }
    }

    @Override
    @TransactionalForException
    public void restore(String channelNo, Long userId, Long id) {
        PageManager pageManager = getById(id);
        Assert.notNull(pageManager, "页面不存在");

        Long versionId = platformVersionService.getDefaultVersionId(channelNo, null);
        List<Long> pageIds = new ArrayList<>();
        List<PageManager> pageManagers;

        if (pageManager.getIsFolder()) {
            // 恢复文件夹
            pageManagers = list(new QueryWrapper<PageManager>().lambda().eq(PageManager::getId, id).or().eq(PageManager::getFolderId, id));
            if (CollectionUtils.isNotEmpty(pageManagers)) {
                pageIds.addAll(pageManagers.stream().map(PageManager::getId).collect(Collectors.toList()));
                pageManagers.forEach(o -> {
                    o.setVersionId(versionId);
                    o.setStatus(CommonConstant.STATUS.DRAFT.getCode());
                    o.setChangeStatus(o.getOldChangeStatus());
                    o.setPageTags(getAlertTag(o, PAGE_TAG_RESTORE));
                });
                updateBatchById(pageManagers);
            }
        } else {
            // 恢复页面
            Long newFolderId = null;
            if (pageManager.getFolderId() != null) {
                PageManager folderPage = getById(pageManager.getFolderId());
                if (folderPage != null) {
                    PageManager newFolder = getByPageUniqueId(versionId, folderPage.getPageUniqueId());
                    newFolderId = newFolder.getId();
                }
            }

            // 单独恢复页面且页面对应的文件夹不存在时放文件夹外面
            update(new PageManager(), Wrappers.<PageManager>lambdaUpdate()
                    .set(PageManager::getVersionId, versionId)
                    .set(PageManager::getChangeStatus, pageManager.getOldChangeStatus())
                    .set(PageManager::getPageTags, getAlertTag(pageManager, PAGE_TAG_RESTORE))
                    .set(PageManager::getFolderId, newFolderId)
                    .set(PageManager::getStatus, CommonConstant.STATUS.DRAFT.getCode())
                    .eq(PageManager::getId, pageManager.getId()));

            pageIds.add(id);
            pageManagers = Collections.singletonList(pageManager);
        }

        // 恢复组件
        List<PageModule> pageModules = pageModuleService.listByPageIds(pageIds);
        if (CollectionUtils.isNotEmpty(pageModules)) {
            Map<Integer, List<PageModule>> moduleMap = pageModules.stream().collect(Collectors.groupingBy(PageModule::getModuleType));
            moduleMap.forEach((k, v) -> moduleService.getInstance(k).restoreModule(v.stream().map(PageModule::getModuleId).collect(Collectors.toList()), versionId));
        }

        // 增加操作日志
        pageOperationLogService.savePageLog(userId, PageCenterConsts.LogOperate.RESTORE, null, pageManagers);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageManager.getVersionId());
    }

    @Override
    @TransactionalForException
    public void updateByName(Long userId, PageManager pageManager, String pageName) {
        // 更新操作人和页面名
        String operatorIds = updateOperator(userId, pageManager);
        update(new PageManager(), Wrappers.<PageManager>lambdaUpdate()
                .set(PageManager::getPageName, pageName)
                .set(StringUtils.isNotBlank(operatorIds), PageManager::getOperatorIds, operatorIds)
                .set(pageManager.getChangeStatus() != ChangerStatus.ADD.ordinal(), PageManager::getChangeStatus, ChangerStatus.ALERT.ordinal())
                .set(pageManager.getChangeStatus() != 0, PageManager::getPageTags, getAlertTag(pageManager, PAGE_TAG_ALERT))
                .eq(PageManager::getId, pageManager.getId()));

        // 增加操作日志
        pageOperationLogService.savePageLog(userId, PageCenterConsts.LogOperate.UPDATE, pageName, pageManager);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageManager.getVersionId());
    }

    @Override
    public List<PageManager> listRecycleBinByChannelNo(String channelNo) {
        return list(new QueryWrapper<PageManager>().lambda()
                .eq(PageManager::getChannelNo, channelNo)
                .eq(PageManager::getStatus, CommonConstant.STATUS.TRASH.getCode())
                .orderByDesc(PageManager::getUpdatedTime));
    }

    @Override
    @TransactionalForException
    public void createChannelDefaultPage(PlatformVersion platformVersion) {
        //创建首页
        PageManager homePage = new PageManager();
        long id = IdWorker.getId(homePage);
        homePage.setChangeStatus(ChangerStatus.ADD.ordinal());
        homePage.setChannelNo(platformVersion.getChannelNo());
        homePage.setPageName("首页");
        homePage.setBusinessType(PageCenterConsts.BusinessType.COMMON.getCode());
        homePage.setIsFolder(false);
        homePage.setVersionId(platformVersion.getId());
        homePage.setOperatorIds(platformVersion.getCreatedBy().toString());
        homePage.setId(id);
        homePage.setPageUniqueId(ConvertUtils.encode62(id));
        homePage.setSortNo(System.currentTimeMillis());
        homePage.setStatus(CommonConstant.STATUS.DRAFT.getCode());
        homePage.setRouterName(ROUTER_NAME_HOME);
        homePage.setPageTags(PAGE_TAG_NEW);
        homePage.setUpdatedBy(platformVersion.getCreatedBy());
        homePage.setIsSupportTemplate(true);
        save(homePage);
        pageOperationLogService.savePageLog(platformVersion.getCreatedBy(), PageCenterConsts.LogOperate.ADD, null, homePage);
        PageTemplate template = pageTemplateService.findByType(ROUTER_NAME_HOME);
        if (template != null) {
            //根据默认的模板配置初始化首页
            applyPageTemplate(homePage, template);
        }
        //创建个人中心页面
        PageManager personalCenter = new PageManager();
        long personalId = IdWorker.getId(personalCenter);
        personalCenter.setChangeStatus(ChangerStatus.ADD.ordinal());
        personalCenter.setChannelNo(platformVersion.getChannelNo());
        personalCenter.setPageName("我的");
        personalCenter.setBusinessType(PageCenterConsts.BusinessType.COMMON.getCode());
        personalCenter.setIsFolder(false);
        personalCenter.setVersionId(platformVersion.getId());
        personalCenter.setOperatorIds(platformVersion.getCreatedBy().toString());
        personalCenter.setId(personalId);
        personalCenter.setPageUniqueId(ConvertUtils.encode62(personalId));
        personalCenter.setSortNo(System.currentTimeMillis());
        personalCenter.setStatus(CommonConstant.STATUS.DRAFT.getCode());
        personalCenter.setRouterName(ROUTER_NAME_PERSONAL);
        personalCenter.setPageTags(PAGE_TAG_NEW);
        personalCenter.setUpdatedBy(platformVersion.getCreatedBy());
        personalCenter.setIsSupportTemplate(false);
        save(personalCenter);
        pageOperationLogService.savePageLog(platformVersion.getCreatedBy(), PageCenterConsts.LogOperate.ADD, null, personalCenter);
        PageTemplate pageTemplate = pageTemplateService.findByType(ROUTER_NAME_PERSONAL);
        if (pageTemplate != null) {
            //根据默认的模板配置初始化个人中心
            applyPageTemplate(personalCenter, pageTemplate);
        }
        //创建默认的首页导航栏
        //TODO 模板外部配置
        String defaultConfig = "{\"belongType\":0,\"blockId\":\"f242c37a-6b87-45f9-8a9e-5ff1e1faee8c\",\"category\":0,\"data\":{\"imgList\":[{\"imgId\":\"1cc17629-c6e5-4909-bdc5-dee12ecc787a\",\"imgUrl\":\"https://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/assets/icon_sy2_grzx.png\",\"link\":{\"businessType\":0,\"linkType\":0,\"pageId\":\"\",\"pageName\":\"首页\",\"pageUrl\":\"\",\"params\":{},\"routerName\":\"home\"},\"title\":\"首页\"},{\"imgId\":\"f85ca7d9-4f40-45a7-b7aa-6500b4208c79\",\"imgUrl\":\"https://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/assets/icon_wd2_grzx.png\",\"link\":{\"businessType\":0,\"linkType\":0,\"pageId\":\"\",\"pageName\":\"我的\",\"pageUrl\":\"\",\"params\":{},\"routerName\":\"personal\"},\"title\":\"我的\"}]},\"isHidden\":false,\"isInitModule\":true,\"layoutType\":1,\"moduleType\":8,\"name\":\"navigation\",\"status\":\"PREVIEW\",\"style\":{}}";
        WebJsonBo webJsonBo = JSONObject.parseObject(defaultConfig, WebJsonBo.class);
        List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
        imgList.get(0).getLink().setPageId(homePage.getPageUniqueId().toString());
        imgList.get(1).getLink().setPageId(personalCenter.getPageUniqueId().toString());
        homeNavService.updateWebJson(webJsonBo, platformVersion);
    }

    @Override
    @TransactionalForException
    public void applyPageTemplate(PageManager pageManager, PageTemplate pageTemplate) {
        Assert.notNull(pageManager, "页面信息不能为空");
        Assert.notNull(pageTemplate, "模板信息不能为空");
        //清除之前的页面组件
        List<PageModule> pageModules = pageModuleService.listByPageId(pageManager.getId());
        if (CollectionUtils.isNotEmpty(pageModules)) {
            pageModules.forEach(o -> {
                // 先删组件，再删关联
                moduleService.getInstance(o.getModuleType()).deletePageModule(new ArrayList<>(Collections.singletonList(o.getModuleId())));
                pageModuleService.removeById(o.getId());
            });
        }
        //应用新的模板内容
        if (StringUtils.isNotEmpty(pageTemplate.getWebJson())) {
            List<JSONObject> array = JSONObject.parseArray(pageTemplate.getWebJson(), JSONObject.class);
            Long lastId = null;
            if (CollectionUtils.isNotEmpty(array)) {
                //页面详情是按id倒序展示的，这里插入内置组件需要做ge
                Collections.reverse(array);
                for (JSONObject moduleConfig : array) {
                    PageModuleRequest pmRequest = new PageModuleRequest();
                    pmRequest.setLastId(lastId);
                    pmRequest.setModuleType(moduleConfig.getInteger("moduleType"));
                    pmRequest.setWebJson(moduleConfig.toJSONString());
                    PageModule pageModule = pageModuleService.saveModule(pageManager.getUpdatedBy(), pageManager, pmRequest);
                    lastId = pageModule.getModuleId();
                }
            }
        }
    }

    @Override
    public List<PageManagerDto> pageList(String channelNo, Integer queryType, Long versionId, Integer businessType, Integer moduleType) {
        if (StringUtils.isNotBlank(channelNo)) {
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
            org.springframework.util.Assert.notNull(platformChannel, "当前渠道已被删除，请重新选择渠道");
        } else {
            return new ArrayList<>();
        }

        if (moduleType == null) {
            PlatformVersion platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);
            Assert.isTrue(channelNo.equals(platformVersion.getChannelNo()), "版本与渠道不一致");
            versionId = platformVersion.getId();
        }

        PageManagerQuery query = new PageManagerQuery();
        query.setChannelNo(channelNo);
        query.setVersionId(versionId);
        query.setBusinessType(businessType);
        query.setQueryType(queryType);
        query.setModuleType(moduleType);
        query.setPageType(0);
        query.setIsHide(0);
        List<PageManager> pageManagers = listByParam(query);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(pageManagers)) {
            return new ArrayList<>();
        }

        Map<Long, List<PageManagerDto>> childrenMap = new HashMap<>(16);
        pageManagers.forEach(o -> {
            if (o.getFolderId() != null) {
                List<PageManagerDto> children = childrenMap.get(o.getFolderId());
                if (children == null) {
                    children = new ArrayList<>();
                }
                PageManagerDto dto = BaseHelper.r2t(o, PageManagerDto.class);
                if (StringUtils.isNotBlank(o.getPageTags())) {
                    dto.setPageTags(Arrays.asList(o.getPageTags().split(",")));
                }

                dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(o.getBusinessType()));
                dto.setRouterName(o.getRouterName());
                children.add(dto);
                childrenMap.put(o.getFolderId(), children);
            }
        });

        return pageManagers.stream().filter(o -> moduleType != null || o.getFolderId() == null).map(o -> {
            PageManagerDto dto = BaseHelper.r2t(o, PageManagerDto.class);
            if (StringUtils.isNotBlank(o.getPageTags())) {
                dto.setPageTags(Arrays.asList(o.getPageTags().split(",")));
            }
            if (moduleType == null) {
                List<PageManagerDto> pageListDtos = childrenMap.get(o.getId());
                dto.setChildren(org.apache.commons.collections4.CollectionUtils.isEmpty(pageListDtos) ? new ArrayList<>() : pageListDtos);
            }
            dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(o.getBusinessType()));
            dto.setRouterName(o.getRouterName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void copyPage(List<PageManager> oldPageManagers, PlatformVersion version) {
        copyPageToChannel(oldPageManagers, version, null);
    }

    @Override
    @TransactionalForException
    public void copyPageToChannel(List<PageManager> oldPageManagers, PlatformVersion version, String newChannelNo) {
        if (CollectionUtils.isEmpty(oldPageManagers)) {
            return;
        }

        List<PageManager> newPageManagers = BaseHelper.r2t(oldPageManagers, PageManager.class);

        // 先复制文件夹
        List<PageManager> newFolder = newPageManagers.stream().filter(PageManager::getIsFolder).peek(o -> {
            o.setSourceId(o.getId());
            o.setChannelNo(StringUtils.isBlank(newChannelNo) ? o.getChannelNo() : newChannelNo);
            o.setId(null);
            o.setVersionId(version.getId());
        }).collect(Collectors.toList());
        saveBatch(newFolder);
        Map<Long, Long> newFolderMap = newFolder.stream().collect(Collectors.toMap(PageManager::getSourceId, PageManager::getId, (x, y) -> y));

        // 再复制页面
        List<PageManager> newPage = newPageManagers.stream().filter(o -> !o.getIsFolder()).peek(o -> {
            o.setSourceId(o.getId());
            o.setId(null);
            o.setChannelNo(StringUtils.isBlank(newChannelNo) ? o.getChannelNo() : newChannelNo);
            o.setChangeStatus(PageCenterConsts.ChangerStatus.NOT_ALERT.ordinal());
            o.setOldChangeStatus(PageCenterConsts.ChangerStatus.NOT_ALERT.ordinal());
            o.setVersionId(version.getId());
            o.setFolderId(newFolderMap.get(o.getFolderId()));
            o.setStatus(CommonConstant.STATUS.DRAFT.getCode());
            o.setOperatorIds(null);

            String pageTags = o.getPageTags();
            o.setPageTags(null);
            if (StringUtils.isNotBlank(pageTags)) {
                if (pageTags.contains(PAGE_TAG_EMPTY)) {
                    o.setPageTags(PAGE_TAG_EMPTY);
                }
            }
        }).collect(Collectors.toList());
        saveBatch(newPage);

        // 复制页面到新渠道需要复制首页导航栏
        if (StringUtils.isNotBlank(newChannelNo)) {
            String homePageUId = newPage.stream().filter(o -> ObjectUtils.nullSafeEquals(o.getRouterName(), ROUTER_NAME_HOME)).findFirst().map(PageManager::getPageUniqueId).get();
            String personPageUId = newPage.stream().filter(o -> ObjectUtils.nullSafeEquals(o.getRouterName(), ROUTER_NAME_PERSONAL)).findFirst().map(PageManager::getPageUniqueId).get();
            //TODO 模板外部配置
            String defaultConfig = "{\"belongType\":0,\"blockId\":\"f242c37a-6b87-45f9-8a9e-5ff1e1faee8c\",\"category\":0,\"data\":{\"imgList\":[{\"imgId\":\"1cc17629-c6e5-4909-bdc5-dee12ecc787a\",\"imgUrl\":\"https://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/assets/icon_sy2_grzx.png\",\"link\":{\"businessType\":0,\"linkType\":0,\"pageId\":\"\",\"pageName\":\"首页\",\"pageUrl\":\"\",\"params\":{},\"routerName\":\"home\"},\"title\":\"首页\"},{\"imgId\":\"f85ca7d9-4f40-45a7-b7aa-6500b4208c79\",\"imgUrl\":\"https://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/assets/icon_wd2_grzx.png\",\"link\":{\"businessType\":0,\"linkType\":0,\"pageId\":\"\",\"pageName\":\"我的\",\"pageUrl\":\"\",\"params\":{},\"routerName\":\"personal\"},\"title\":\"我的\"}]},\"isHidden\":false,\"isInitModule\":true,\"layoutType\":1,\"moduleType\":8,\"name\":\"navigation\",\"status\":\"PREVIEW\",\"style\":{}}";
            WebJsonBo webJsonBo = JSONObject.parseObject(defaultConfig, WebJsonBo.class);
            List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
            imgList.get(0).getLink().setPageId(homePageUId);
            imgList.get(1).getLink().setPageId(personPageUId);
            homeNavService.updateWebJson(webJsonBo, version);
        }


        Map<Long, Long> newPageMap = newPage.stream().collect(Collectors.toMap(PageManager::getSourceId, PageManager::getId, (x, y) -> y));

        // 复制组件
        List<PageModule> oldPageModules = pageModuleService.listByPageIds(oldPageManagers.stream().map(PageManager::getId).collect(Collectors.toList()));
        List<PageModule> newPageModules = oldPageModules.stream().map(o -> {
            PageModule newPageModule = BaseHelper.r2t(o, PageModule.class);
            newPageModule.setId(null);
            newPageModule.setChannelNo(StringUtils.isBlank(newChannelNo) ? o.getChannelNo() : newChannelNo);
            newPageModule.setPageId(newPageMap.get(o.getPageId()));
            newPageModule.setVersionId(version.getId());

            BaseModuleService instance = moduleService.getInstance(o.getModuleType());
            String webJson = ObjectUtils.nullSafeEquals(o.getModuleType(), CommonConstant.ModuleType.EMPTY_BUSINESS.getCode()) || instance == null ?
                    o.getWebJson() : instance.clonePageModule(o.getModuleId(), newPageModule.getPageId());
            newPageModule.setModuleId(JSON.parseObject(webJson).getLong("id"));
            newPageModule.setWebJson(webJson);

            // 更新老组件状态
            if (instance != null) {
                instance.updateModuleStatus(o.getModuleId(), CommonConstant.STATUS.PUBLISHED.getCode());
            }
            return newPageModule;
        }).collect(Collectors.toList());
        pageModuleService.saveBatch(newPageModules);
    }

    @Override
    public PageManager getByRouterName(String routerName, String channelNo, Long versionId) {
        Assert.isTrue(StringUtils.isNotBlank(routerName), "路由名不能为空");
        if (versionId == null) {
            Assert.isTrue(StringUtils.isNotBlank(channelNo), "渠道号不能为空");
            versionId = platformChannelService.getReleaseVersionIdByChannelNo(channelNo);
        }

        return getOne(Wrappers.<PageManager>lambdaQuery()
                .eq(PageManager::getRouterName, routerName)
                .eq(PageManager::getVersionId, versionId)
                .last("limit 1"));
    }
}
