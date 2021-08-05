package com.touchealth.platform.processengine.service.impl.page;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.dao.page.PageOperationLogDao;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageOperationLog;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.query.ModuleLogQuery;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageOperationLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.CombinationType.TYPE_RELEASE_A;

/**
 * @author liufengqiang
 * @date 2020-11-24 15:48:24
 */
@Service
public class PageOperationLogServiceImpl extends BaseServiceImpl<PageOperationLogDao, PageOperationLog> implements PageOperationLogService {

    @Resource
    private PageManagerService pageManagerService;

    @Override
    public void savePageLog(Long userId, PageCenterConsts.LogOperate operate, String newPageName, PageManager pageManager) {
        String folderName = null;
        if (pageManager.getFolderId() != null) {
            PageManager folder = pageManagerService.getById(pageManager.getFolderId());
            folderName = folder.getPageName();
        }
        save(getPageOperationLog(userId, folderName, pageManager, operate, newPageName));
    }

    @Override
    public void savePageLog(Long userId, PageCenterConsts.LogOperate operate, String newPageName, Collection<PageManager> pageManagers) {
        Map<Long, String> folderMap = pageManagerService.getFolderMap(pageManagers);
        saveBatch(pageManagers.stream().map(o -> getPageOperationLog(userId, folderMap.get(o.getFolderId()), o, operate, newPageName)).collect(Collectors.toList()));
    }

    private PageOperationLog getPageOperationLog(Long userId, String folderName, PageManager pageManager, PageCenterConsts.LogOperate operate, String newPageName) {
        PageOperationLog pageOperationLog = new PageOperationLog();
        pageOperationLog.setChannelNo(pageManager.getChannelNo());
        pageOperationLog.setVersionId(pageManager.getVersionId());
        pageOperationLog.setPageId(pageManager.getId());
        pageOperationLog.setFolderName(folderName);
        pageOperationLog.setPageName(pageManager.getPageName());
        pageOperationLog.setOperationNo(UUID.randomUUID().toString());
        pageOperationLog.setUpdatedBy(userId);

        if (PageCenterConsts.LogOperate.ADD.equals(operate) || PageCenterConsts.LogOperate.DELETE.equals(operate)) {
            pageOperationLog.setCombinationType(pageManager.getIsFolder() ? PageCenterConsts.CombinationType.TYPE_FOLDER_A.ordinal()
                    : PageCenterConsts.CombinationType.TYPE_PAGE_A.ordinal());
        } else {
            pageOperationLog.setCombinationType(pageManager.getIsFolder() ? PageCenterConsts.CombinationType.TYPE_FOLDER_B.ordinal()
                    : PageCenterConsts.CombinationType.TYPE_PAGE_B.ordinal());
        }
        pageOperationLog.setOperate(operate.getValue());
        pageOperationLog.setContent(pageManager.getPageName());

        if (PageCenterConsts.LogOperate.RESTORE.getValue().equals(operate.getValue())) {
            pageOperationLog.setInitValue("回收站状态");
            pageOperationLog.setFinishValue("草稿状态");
        } else {
            pageOperationLog.setInitValue(pageManager.getPageName());
            pageOperationLog.setFinishValue(pageManager.getPageName());
        }

        pageOperationLog.setName("");
        if (PageCenterConsts.LogOperate.UPDATE.equals(operate)) {
            pageOperationLog.setName("名称");
            pageOperationLog.setFinishValue(newPageName);
        }
        return pageOperationLog;
    }

    @Override
    public void saveReleaseLog(Long userId, PlatformVersion oldVersion, String newVersionName) {
        PageOperationLog pageOperationLog = new PageOperationLog();
        pageOperationLog.setCombinationType(TYPE_RELEASE_A.ordinal());
        pageOperationLog.setOperationNo(UUID.randomUUID().toString());
        pageOperationLog.setUpdatedBy(userId);
        pageOperationLog.setChannelNo(oldVersion.getChannelNo());
        pageOperationLog.setVersionId(oldVersion.getId());
        pageOperationLog.setOperate(PageCenterConsts.LogOperate.RELEASE.getValue());
        pageOperationLog.setContent("产品");
        pageOperationLog.setInitValue(oldVersion.getVersionName());
        pageOperationLog.setFinishValue(newVersionName);
        save(pageOperationLog);
    }

    @Override
    public void saveModuleLog(ModuleLogQuery query) {
        if (CollectionUtils.isNotEmpty(query.getCompareBos())) {
            if (PageCenterConsts.LogOperate.ADD.equals(query.getOperate())) {
                CompareBo compareBo = new CompareBo();
                compareBo.setModifyAction(PageCenterConsts.LogOperate.ADD.getValue());
                compareBo.setCombinationType(PageCenterConsts.CombinationType.TYPE_COMPONENT_B.ordinal());
                query.getCompareBos().add(0, compareBo);
            }

            String operationNo = UUID.randomUUID().toString();
            List<PageOperationLog> pageOperationLogs = query.getCompareBos().stream().map(o -> {
                PageOperationLog pageOperationLog = new PageOperationLog();
                if (PageCenterConsts.CombinationType.TYPE_COMPONENT_B.getCode().equals(o.getCombinationType())) {
                    pageOperationLog.setCombinationType(PageCenterConsts.CombinationType.TYPE_COMPONENT_B.ordinal());
                } else {
                    pageOperationLog.setCombinationType(PageCenterConsts.CombinationType.TYPE_COMPONENT_A.ordinal());
                }

                pageOperationLog.setChannelNo(query.getPageManager().getChannelNo());
                pageOperationLog.setVersionId(query.getPageManager().getVersionId());
                pageOperationLog.setPageId(query.getPageManager().getId());
                pageOperationLog.setModuleId(query.getPageModule().getId());
                pageOperationLog.setFolderName(query.getFolderName());
                pageOperationLog.setPageName(query.getPageManager().getPageName());
                pageOperationLog.setOperationNo(operationNo);
                pageOperationLog.setUpdatedBy(query.getUserId());

                pageOperationLog.setModuleName(CommonConstant.ModuleType.getNameByCode(query.getPageModule().getModuleType()) + "组件");
                pageOperationLog.setOperate(o.getModifyAction());
                pageOperationLog.setContent(o.getModifyName());
                pageOperationLog.setName(o.getModifyActionContent());
                pageOperationLog.setInitValue(o.getModifyBeforeValue());
                pageOperationLog.setFinishValue(o.getModifyAfterValue());
                return pageOperationLog;
            }).collect(Collectors.toList());
            saveBatch(pageOperationLogs);
        } else {
            log.error("新增组件操作日志失败，组件对比值为空");
        }
    }

    @Override
    public List<PageOperationLog> listByVersionId(Long versionId) {
        return list(Wrappers.<PageOperationLog>lambdaQuery()
                .eq(PageOperationLog::getVersionId, versionId)
                .orderByDesc(PageOperationLog::getCreatedTime)
                .orderByAsc(PageOperationLog::getId));
    }

    @Override
    public List<PageOperationLog> listByModuleIdAndName(Long moduleId, String name) {
        return list(Wrappers.<PageOperationLog>lambdaQuery()
                .eq(PageOperationLog::getModuleId, moduleId)
                .eq(PageOperationLog::getName, name));
    }
}
