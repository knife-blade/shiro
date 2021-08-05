package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.ComboImg;
import com.touchealth.platform.processengine.pojo.bo.module.common.ComboImgBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDto;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.List;

/**
 * <p>
 * 组合图通用组件表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
public interface ComboImgService extends BaseModuleService<ComboImg> {

    ComboImgDto save(ComboImgBo bo);

    String update(ComboImgBo bo);

    ComboImgDto findById(Long id,Boolean showRecycleBin);

    Boolean delete(Long id);

    Boolean delete(List<Long> id);

    List<ComboImgDto> findByIdList(List<Long> ids,Boolean showRecycleBin);

    /**
     * 批量更新组件状态
     *
     * @param moduleIds 组件id
     * @param status   版本状态
     * @return
     */
    Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status);

    /**
     * 批量更新组件状态
     *
     * @param moduleIds 组件id
     * @param status   版本状态
     * @param versionId   版本Id
     * @return
     */
    Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId);
}
