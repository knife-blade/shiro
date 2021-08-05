package com.touchealth.platform.processengine.service.page;

import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.pojo.request.page.PageModuleRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * 页面-组件关联表
 *
 * @author liufengqiang
 * @date 2020-11-20 11:28:34
 */
public interface PageModuleService extends BaseService<PageModule> {

    /**
     * 新增组件
     *
     * @param userId
     * @param pageManager
     * @param request return
     * @return
     */
    PageModule saveModule(Long userId, PageManager pageManager, PageModuleRequest request);

    /**
     * 删除组件
     *
     * @param userId
     * @param componentId
     */
    void deleteModule(Long userId, Long componentId);

    /**
     * 更新组件
     *
     * @param userId
     * @param pageModule
     * @param request
     * @return
     */
    String updateModule(Long userId, PageModule pageModule, PageModuleRequest request);

    /**
     * 根据页面id查询
     *
     * @param pageId
     * @return
     */
    List<PageModule> listByPageId(Long pageId);

    /**
     * 根据页面id查询
     *
     * @param pageIds
     * @return
     */
    List<PageModule> listByPageIds(Collection<Long> pageIds);

    /**
     * 根据pageId统计组件
     *
     * @param pageId
     * @return
     */
    int countByPageId(Long pageId);

    /**
     * 根据渠道号查询
     * @param channelNo
     * @return
     */
    List<PageModule> listByChannelNo(String channelNo);

    /**
     * 获取对应的模块信息
     * @param channelNo 渠道code
     * @param versionId 版本ID
     * @param moduleType
     * @return
     */
    PageModule findByChannelNoAndVersionIdAndModuleType(String channelNo, Long versionId, Integer moduleType);

    PageModule getByModuleIdAndVersion(Long moduleId, Long releaseVersion);
}
