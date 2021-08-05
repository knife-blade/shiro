package com.touchealth.platform.processengine.dao.module.common;

import com.touchealth.platform.processengine.entity.module.common.NavigateImg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;

import java.util.List;

/**
 * <p>
 * 坑位导航通用组件表之图片内容表 Mapper 接口
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
public interface NavigateImgDao extends BaseMapper<NavigateImg> {

    List<NavigateImgDto> queryList(NavigateRequest request);
}
