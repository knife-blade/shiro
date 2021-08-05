package com.touchealth.platform.processengine.dao.module.common;

import com.touchealth.platform.processengine.entity.module.common.HotspotParts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotPartsDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HotspotRequest;

import java.util.List;

/**
 * <p>
 * 热区通用组件表之热区部分表 Mapper 接口
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
public interface HotspotPartsDao extends BaseMapper<HotspotParts> {

    List<HotspotPartsDto> getList(HotspotRequest request);
}
