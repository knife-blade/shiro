package com.touchealth.platform.processengine.service.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.Hotspot;
import com.touchealth.platform.processengine.pojo.bo.module.common.HotspotBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HotspotRequest;
import com.touchealth.platform.processengine.service.BaseService;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.List;

/**
 * <p>
 * 热区组件表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
public interface HotspotService extends BaseModuleService<Hotspot> {

    HotspotDto save(HotspotBo bo);

    String update(HotspotBo bo);

    HotspotDto findById(Long id,Boolean showRecycleBin);

    List<HotspotDto> findByIdList(List<Long> ids,Boolean showRecycleBin);

    Boolean delete(Long id);

    Boolean delete(List<Long> ids);

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
     * @param versionId   版本号
     * @return
     */
    Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId);
}
