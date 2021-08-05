package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.Banner;
import com.touchealth.platform.processengine.pojo.bo.module.common.BannerBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerDto;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.List;

/**
 * <p>
 * 轮播图组件表 服务类
 * </p>
 *
 * @author Xxx
 * @since 2020-11-23
 */
public interface BannerService extends BaseModuleService<Banner> {

    BannerDto save(BannerBo bo);

    String update(BannerBo bo);

    BannerDto findById(Long id,Boolean showRecycleBin);

    List<BannerDto> findByIdList(List<Long> ids,Boolean showRecycleBin);

    Boolean delete(Long id);

    Boolean delete(List<Long> id);

    /**
     * 查询banner详情
     * @param bannerId bannerId
     * @param showUnStart  是否展示未开始的 0 ：展示 1：不展示
     * @return
     */
    BannerDto queryBannerDetail(Long bannerId, Integer showUnStart);

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
     * @return
     */
    Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId);
}
