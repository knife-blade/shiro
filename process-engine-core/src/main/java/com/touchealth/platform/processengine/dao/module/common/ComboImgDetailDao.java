package com.touchealth.platform.processengine.dao.module.common;

import com.touchealth.platform.processengine.entity.module.common.ComboImgDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDetailDto;
import com.touchealth.platform.processengine.pojo.request.module.common.ComboImgDetailRequest;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;

import java.util.List;

/**
 * <p>
 * 组合图通用组件表之图片内容表 Mapper 接口
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
public interface ComboImgDetailDao extends BaseMapper<ComboImgDetail> {

    List<ComboImgDetailDto> queryList(ComboImgDetailRequest request);
}
