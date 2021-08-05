package com.touchealth.platform.processengine.dao.module.common;

import com.touchealth.platform.processengine.entity.module.common.BannerImg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.BannerImgRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 轮播图通用组件表之图片内容表 Mapper 接口
 * </p>
 *
 * @author Xxx
 * @since 2020-11-23
 */
public interface BannerImgDao extends BaseMapper<BannerImg> {

    List<BannerImgDto> queryBannerImgList(BannerImgRequest request);

    /**
     * 获取banner图片
     * @param bannerId
     * @param queryType 查询类型：0:后台查询（显示未开始的banner） 1：手机端查询（不展示未开始的banner）
     * @return
     */
    List<BannerImgDto> queryBannerImgByBannerId(@Param("bannerId") Long bannerId, @Param("showUnStart") Integer showUnStart);
}
