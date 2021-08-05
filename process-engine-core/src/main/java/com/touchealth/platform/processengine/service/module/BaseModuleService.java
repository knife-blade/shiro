package com.touchealth.platform.processengine.service.module;

import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * @param <T> 组件实体
 * @author liufengqiang
 * @date 2020-11-21 09:56:58
 */
public interface BaseModuleService<T> extends BaseService<T> {

    /**
     * 添加页面的组件
     *
     * @param webJson
     * @param pageId
     * @return
     */
    String savePageModule(String webJson, Long pageId);

    /**
     * 复制页面 组件
     *
     * @param moduleId
     * @param pageId
     * @return
     */
    String clonePageModule(Long moduleId, Long pageId);

    /**
     * 更新页面的组件内容
     *
     * @param webJson
     * @return
     */
    String updatePageModule(String webJson);

    /**
     * 更新组件状态
     *
     * @param moduleId 组件id
     * @param status   版本状态
     * @return
     */
    Boolean updateModuleStatus(Long moduleId, Integer status);

    /**
     * 根据id查询页面的组件
     *
     * 已废弃，请使用 #getModuleById
     *
     * @param id
     * @return
     */
    @Deprecated
    String findPageModuleById(Long id);

    /**
     * 根据id查询页面的组件
     *
     * @param id
     * @param param
     * @return
     */
    String getModuleById(Long id, String... param);

    /**
     * 根据ids批量查询组件
     *
     * @param ids
     * @return
     */
    List<String> findPageModuleByIdList(List<Long> ids);

    /**
     * 删除页面中的组件
     * @param id
     * @return
     */
    Boolean deletePageModule(Long id);

    /**
     * 批量删除页面中的组件
     * @param ids
     * @return
     */
    Boolean deletePageModule(List<Long> ids);

    /**
     * 恢复组件
     *
     * @param ids
     * @return
     */
    Boolean restoreModule(List<Long> ids);

    /**
     * 恢复组件
     * @param ids
     * @param versionId 恢复的版本
     * @return
     */
    Boolean restoreModule(Collection<Long> ids, Long versionId);

    /**
     * 组件修改前后比对方法（仅编辑组件时使用）
     *
     * @param webJson
     * @param moduleId
     * @return
     */
    List<CompareBo> compare(String webJson, Long moduleId);
}
