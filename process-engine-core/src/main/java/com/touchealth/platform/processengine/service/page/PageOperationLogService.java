package com.touchealth.platform.processengine.service.page;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageOperationLog;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.query.ModuleLogQuery;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * 页面操作日志
 *
 * @author liufengqiang
 * @date 2020-11-24 15:47:29
 */
public interface PageOperationLogService extends BaseService<PageOperationLog> {

    /**
     * 新增页面日志
     * @param userId
     * @param operate
     * @param newPageName
     * @param pageManager
     */
    void savePageLog(Long userId, PageCenterConsts.LogOperate operate, String newPageName, PageManager pageManager);

    /**
     * 批量新增页面日志
     * @param userId
     * @param operate
     * @param newPageName
     * @param pageManagers
     */
    void savePageLog(Long userId, PageCenterConsts.LogOperate operate, String newPageName, Collection<PageManager> pageManagers);

    /**
     * 新增组件日志
     * @param query
     */
    void saveModuleLog(ModuleLogQuery query);

    /**
     * 新增发布日志
     * @param userId
     * @param oldVersion
     * @param newVersionName
     */
    void saveReleaseLog(Long userId, PlatformVersion oldVersion, String newVersionName);

    /**
     * 根据版本号查询
     * @param versionId
     * @return
     */
    List<PageOperationLog> listByVersionId(Long versionId);

    /**
     * 根据组件id和操作名查询数据
     * @param moduleId
     * @param name
     * @return
     */
    List<PageOperationLog> listByModuleIdAndName(Long moduleId, String name);
}
