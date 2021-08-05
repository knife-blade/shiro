package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.Navigate;
import com.touchealth.platform.processengine.pojo.bo.module.common.NavigateBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateDto;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 坑位导航组件表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
public interface NavigateService extends BaseService<Navigate> {

    NavigateDto save(NavigateBo bo);

    String update(NavigateBo bo);

    NavigateDto findById(Long id,Boolean showRecycleBin);

    Boolean delete(Long id);

    Boolean delete(List<Long> ids);

    List<NavigateDto> findByIdList(List<Long> ids,Boolean showRecycleBin);

    /**
     * 批量更新组件状态
     *
     * @param moduleId 组件id
     * @param status   版本状态
     * @return
     */
    Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status);

    /**
     * 批量更新组件状态
     *
     * @param moduleId 组件id
     * @param status   版本状态
     * @return
     */
    Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId);
}
