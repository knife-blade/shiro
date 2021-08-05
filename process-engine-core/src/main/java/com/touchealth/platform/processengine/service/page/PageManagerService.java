package com.touchealth.platform.processengine.service.page;

import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.dto.page.PageManagerDto;
import com.touchealth.platform.processengine.pojo.query.PageManagerQuery;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author liufengqiang
 * @date 2020-11-18 15:12:31
 */
public interface PageManagerService extends BaseService<PageManager> {

    /**
     * 新增
     *
     * @param userId
     * @param pageManager
     */
    void savePage(Long userId, PageManager pageManager);

    /**
     * 批量新增
     *
     * @param userId
     * @param pageManagers
     */
    void savePage(Long userId, Collection<PageManager> pageManagers);

    /**
     * 放入回收站
     *
     * @param userId
     * @param id
     */
    void recycleBin(Long userId, Long id);

    /**
     * 回收站恢复
     *
     * @param channelNo
     * @param userId
     * @param id
     */
    void restore(String channelNo, Long userId, Long id);

    /**
     * 更新页面名称
     *
     * @param userId
     * @param pageManager
     * @param pageName
     */
    void updateByName(Long userId, PageManager pageManager, String pageName);

    /**
     * 更新操作人
     *
     * @param userId
     * @param pageManager
     * @return
     */
    String updateOperator(Long userId, PageManager pageManager);

    /**
     * 更新页面状态
     *
     * @param userId
     * @param pageManager
     */
    void updateChangeStatus(Long userId, PageManager pageManager);

    /**
     * 根据pageUniqueId查询页面
     *
     * @param versionId
     * @param pageUniqueId
     * @return
     */
    PageManager getByPageUniqueId(Long versionId, String pageUniqueId);

    /**
     * 根据渠道查询页面列表
     *
     * @param query
     * @return
     */
    List<PageManager> listByParam(PageManagerQuery query);

    /**
     * 查询回收站数据
     *
     * @param channelNo
     * @return
     */
    List<PageManager> listRecycleBinByChannelNo(String channelNo);

    /**
     * 查询指定版本所有页面
     *
     * @param versionId
     * @param isDeleted    是否删除（回收站） true 查询删除数据 false 查询未删除数据 null 查询所有数据
     * @param businessType 业务类型 不要这个条件传null就好
     * @return
     */
    List<PageManager> listByVersionId(Long versionId, Boolean isDeleted, Collection<Integer> businessType);

    /**
     * 更新所属文件夹
     *
     * @param pageManager
     * @param folderId
     */
    void updateFolder(PageManager pageManager, Long folderId);

    /**
     * 根据businessTypes查询数据
     *
     * @param versionId
     * @param businessTypes
     * @return
     */
    List<PageManager> listByBusinessTypes(Long versionId, Collection<Integer> businessTypes);

    /**
     * 获取文件夹名称
     *
     * @param pageManager
     * @return
     */
    String getFolderNameById(PageManager pageManager);

    /**
     * 获取文件夹Map
     *
     * @param pageManagers
     * @return
     */
    Map<Long, String> getFolderMap(Collection<PageManager> pageManagers);

    /**
     * 创建默认的页面
     *
     * @param platformVersion 平台版本信息
     */
    void createChannelDefaultPage(PlatformVersion platformVersion);

    /**
     * 应用页面模板
     *
     * @param pageManager  页面信息
     * @param pageTemplate 页面模板信息
     */
    void applyPageTemplate(PageManager pageManager, PageTemplate pageTemplate);

    /**
     * 页面列表
     *
     * @param channelNo
     * @param queryType
     * @param versionId
     * @param businessType
     * @param moduleType
     * @return
     */
    List<PageManagerDto> pageList(String channelNo, Integer queryType, Long versionId, Integer businessType, Integer moduleType);

    /**
     * 复制页面
     *
     * @param oldPageManagers 要复制的页面
     * @param version         复制保存的版本
     */
    void copyPage(List<PageManager> oldPageManagers, PlatformVersion version);

    /**
     * 复制页面到新的渠道
     *
     * @param oldPageManagers
     * @param version
     * @param newChannelNo
     */
    void copyPageToChannel(List<PageManager> oldPageManagers, PlatformVersion version, String newChannelNo);

    /**
     * 根据路由名查询页面，versionId 为空时根据 channelNo 查询
     *
     * @param routerName
     * @param channelNo
     * @param versionId  为空时查询线上版本
     * @return
     */
    PageManager getByRouterName(String routerName, String channelNo, Long versionId);
}
