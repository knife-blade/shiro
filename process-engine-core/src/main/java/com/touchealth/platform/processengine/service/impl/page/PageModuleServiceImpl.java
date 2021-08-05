package com.touchealth.platform.processengine.service.impl.page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.dao.page.PageComponentDao;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.query.ModuleLogQuery;
import com.touchealth.platform.processengine.pojo.request.page.PageModuleRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageModuleService;
import com.touchealth.platform.processengine.service.page.PageOperationLogService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liufengqiang
 * @date 2020-11-20 11:31:07
 */
@Slf4j
@Service
public class PageModuleServiceImpl extends BaseServiceImpl<PageComponentDao, PageModule> implements PageModuleService {

    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PageOperationLogService pageOperationLogService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private PlatformVersionService platformVersionService;

    @Override
    @TransactionalForException
    public PageModule saveModule(Long userId, PageManager pageManager, PageModuleRequest request) {
        // 新增页面-组件关联
        PageModule pageModule = new PageModule();
        pageModule.setId(IdWorker.getId(pageManager));
        pageModule.setChannelNo(pageManager.getChannelNo());
        pageModule.setVersionId(pageManager.getVersionId());
        pageModule.setPageId(pageManager.getId());
        pageModule.setSortNo(System.currentTimeMillis());
        pageModule.setModuleType(request.getModuleType());
        pageModule.setOperatorIds(String.valueOf(userId));

        // 新增组件
        if (!CommonConstant.ModuleType.EMPTY_BUSINESS.getCode().equals(request.getModuleType())) {
            String webJson = moduleService.getInstance(request.getModuleType()).savePageModule(request.getWebJson(), pageManager.getId());
            Assert.isTrue(StringUtils.isNotBlank(webJson), "新增组件失败");
            JSONObject webJsonObject = JSON.parseObject(webJson);
            pageModule.setWebJson(webJson);
            pageModule.setModuleId(webJsonObject.getLong("id"));
            pageModule.setModuleUniqueId(webJsonObject.getLong("moduleUniqueId"));
        } else {
            JSONObject webJson = JSON.parseObject(request.getWebJson());
            webJson.put("blockId", UUID.randomUUID());
            webJson.put("id", pageModule.getId());
            webJson.put("moduleUniqueId", pageModule.getId());
            pageModule.setWebJson(webJson.toJSONString());
        }
        save(pageModule);

        if (!CommonConstant.ModuleType.LOGIN.getCode().equals(request.getModuleType())) {
            // 更新页面状态
            pageManagerService.updateChangeStatus(userId, pageManager);

            // 重排序整个页面组件
            List<PageModule> pageComponents = listByPageId(pageManager.getId());
            if (!CollectionUtils.isEmpty(pageComponents)) {
                // 拖拽页面
                int swap0 = 0;
                // 拖拽目的
                Integer swap1 = null;
                for (int i = 0; i < pageComponents.size(); i++) {
                    if (pageComponents.get(i).getId().equals(pageModule.getId())) {
                        swap0 = i;
                    }
                    if (pageComponents.get(i).getId().equals(request.getLastId())) {
                        swap1 = i;
                    }
                }
                PageModule addPage = pageComponents.get(swap0);
                if (swap1 != null && swap0 < swap1) {
                    pageComponents.add(swap1 + 1, addPage);
                    pageComponents.remove(swap0);
                } else {
                    pageComponents.add(swap1 == null ? 0 : swap1 + 1, addPage);
                    pageComponents.remove(swap0 + 1);
                }
                Collections.reverse(pageComponents);
                pageComponents.forEach(o -> {
                    o.setSortNo(System.currentTimeMillis());
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                updateBatchById(pageComponents);
            }
        }

        // 新增组件操作日志
        ModuleLogQuery query = new ModuleLogQuery();
        query.setPageModule(pageModule);
        query.setPageManager(pageManager);
        query.setUserId(userId);
        query.setOperate(PageCenterConsts.LogOperate.ADD);
        if (!CommonConstant.ModuleType.EMPTY_BUSINESS.getCode().equals(request.getModuleType())) {
            query.setCompareBos(moduleService.getInstance(request.getModuleType()).compare(request.getWebJson(), null));
        }
        query.setFolderName(pageManagerService.getFolderNameById(pageManager));
        pageOperationLogService.saveModuleLog(query);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageManager.getVersionId());
        return pageModule;
    }

    @Override
    @TransactionalForException
    public void deleteModule(Long userId, Long id) {
        PageModule pageModule = getById(id);
        Assert.notNull(pageModule, "组件不存在");
        PageManager pageManager = pageManagerService.getById(pageModule.getPageId());
        Assert.notNull(pageModule, "页面不存在");

        // 先删组件，再删关联
        moduleService.getInstance(pageModule.getModuleType()).deletePageModule(new ArrayList<>(Collections.singletonList(pageModule.getModuleId())));
        removeById(id);

        // 更新页面状态
        pageManagerService.updateChangeStatus(userId, pageManager);

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageModule.getVersionId());

        // 增加操作日志
        ModuleLogQuery query = new ModuleLogQuery();
        query.setPageManager(pageManager);
        query.setPageModule(pageModule);
        query.setUserId(userId);
        query.setOperate(PageCenterConsts.LogOperate.DELETE);

        CompareBo compareBo = new CompareBo();
        compareBo.setModifyAction(PageCenterConsts.LogOperate.DELETE.getValue());
        compareBo.setCombinationType(PageCenterConsts.CombinationType.TYPE_COMPONENT_B.getCode());
        query.setCompareBos(Collections.singletonList(compareBo));
        query.setFolderName(pageManagerService.getFolderNameById(pageManager));
        pageOperationLogService.saveModuleLog(query);
    }

    @Override
    @TransactionalForException
    public String updateModule(Long userId, PageModule pageModule, PageModuleRequest request) {
        PageManager pageManager = pageManagerService.getById(pageModule.getPageId());
        Assert.notNull(pageModule, "页面不存在");

        // 更新页面状态
        pageManagerService.updateChangeStatus(userId, pageManager);

        // 新增日志
        ModuleLogQuery query = new ModuleLogQuery();
        query.setPageManager(pageManager);
        query.setPageModule(pageModule);
        query.setUserId(userId);
        query.setOperate(PageCenterConsts.LogOperate.UPDATE);
        if (!CommonConstant.ModuleType.EMPTY_BUSINESS.getCode().equals(pageModule.getModuleType())) {
            query.setCompareBos(moduleService.getInstance(pageModule.getModuleType()).compare(request.getWebJson(), pageModule.getModuleId()));
        }
        query.setFolderName(pageManagerService.getFolderNameById(pageManager));
        pageOperationLogService.saveModuleLog(query);

        // 更新组件
        String webJson = moduleService.getInstance(request.getModuleType()).updatePageModule(request.getWebJson());
        // 更新webJson
        update(new PageModule(), Wrappers.<PageModule>lambdaUpdate().set(PageModule::getWebJson, webJson).eq(PageModule::getId, pageModule.getId()));

        // 更新草稿版本时间
        platformVersionService.updateVersionTime(pageManager.getVersionId());
        return webJson;
    }

    @Override
    public List<PageModule> listByPageId(Long pageId) {
        return list(new QueryWrapper<PageModule>().lambda().eq(PageModule::getPageId, pageId).orderByDesc(PageModule::getSortNo));
    }

    @Override
    public List<PageModule> listByPageIds(Collection<Long> pageIds) {
        return list(new QueryWrapper<PageModule>().lambda().in(PageModule::getPageId, pageIds));
    }

    @Override
    public int countByPageId(Long pageId) {
        return count(Wrappers.<PageModule>lambdaQuery().eq(PageModule::getPageId, pageId));
    }

    @Override
    public List<PageModule> listByChannelNo(String channelNo) {
        return list(Wrappers.<PageModule>lambdaQuery().eq(PageModule::getChannelNo, channelNo));
    }

    @Override
    public PageModule findByChannelNoAndVersionIdAndModuleType(String channelNo, Long versionId, Integer moduleType) {

        List<PageModule> list = list(Wrappers.<PageModule>lambdaQuery()
                .eq(channelNo != null, PageModule::getChannelNo, channelNo)
                .eq(versionId != null, PageModule::getVersionId, versionId)
                .eq(moduleType != null, PageModule::getModuleType, moduleType).orderByDesc(PageModule::getId));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public PageModule getByModuleIdAndVersion(Long moduleId, Long releaseVersion) {
        List<PageModule> list = list(Wrappers.<PageModule>lambdaQuery()
                .eq(moduleId != null, PageModule::getModuleId, moduleId)
                .eq(releaseVersion != null, PageModule::getVersionId, releaseVersion)
                .orderByDesc(PageModule::getId));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
