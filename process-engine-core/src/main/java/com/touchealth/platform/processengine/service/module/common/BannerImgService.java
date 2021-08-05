package com.touchealth.platform.processengine.service.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.BannerImg;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.BannerImgRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 轮播图通用组件表之图片内容表 服务类
 * </p>
 *
 * @author Xxx
 * @since 2020-11-23
 */
public interface BannerImgService extends BaseService<BannerImg> {

    PageInfo<BannerImgDto> queryBannerImgList(BannerImgRequest request);

    /**
     *
     * @param channelNo
     * @param bannerId
     * @param showUnStart 是否展示未开始的 0 ：展示 1：不展示
     * @return
     */
    List<BannerImgDto> queryBannerImgByBannerId(Long bannerId, Integer showUnStart);

    /**
     * 根据bannerID获取对应的图片信息
     * @param bannerId ID
     * @return
     */
    List<BannerImg> findByBannerId(Long bannerId);
}
