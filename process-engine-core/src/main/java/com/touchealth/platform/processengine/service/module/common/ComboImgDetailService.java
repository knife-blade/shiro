package com.touchealth.platform.processengine.service.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.ComboImgDetail;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDetailDto;
import com.touchealth.platform.processengine.pojo.request.module.common.ComboImgDetailRequest;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.BaseService;

/**
 * <p>
 * 组合图通用组件表之图片内容表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
public interface ComboImgDetailService extends BaseService<ComboImgDetail> {

    PageInfo<ComboImgDetailDto> queryList(ComboImgDetailRequest request);
}
