package com.touchealth.platform.processengine.service.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.NavigateImg;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 坑位导航通用组件表之图片内容表 服务类
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
public interface NavigateImgService extends BaseService<NavigateImg> {

    PageInfo<NavigateImgDto> queryList(NavigateRequest request);

    /**
     * 根究navigateId查询对应的元素信息
     * @param navigateId ID
     * @return
     */
    List<NavigateImg> findByNavigateId(Long navigateId);
}
