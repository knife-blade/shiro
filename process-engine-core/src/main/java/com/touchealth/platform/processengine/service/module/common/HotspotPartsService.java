package com.touchealth.platform.processengine.service.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.HotspotParts;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotPartsDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HotspotRequest;
import com.touchealth.platform.processengine.service.BaseService;

/**
 * <p>
 * 热区通用组件表之热区部分表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
public interface HotspotPartsService extends BaseService<HotspotParts> {

    PageInfo<HotspotPartsDto> getList(HotspotRequest request);
}
