package com.touchealth.platform.processengine.controller.module.common;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.BannerImgRequest;
import com.touchealth.platform.processengine.service.module.common.BannerImgService;
import com.touchealth.platform.processengine.service.module.common.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@Slf4j
public class BannerController {

    @Resource
    private BannerImgService bannerImgService;

    @Resource
    private BannerService bannerService;

    /**
     * 后台轮播图列表
     * @param channelId
     * @param request
     * @return
     */
    @GetMapping("/bannerImgList")
    public PageInfo<BannerImgDto> queryBannerImgList(@RequestHeader String channelNo,  BannerImgRequest request){
        request.setChannelNo(channelNo);
        return bannerImgService.queryBannerImgList(request);
    }

    /**
     * 查询banner详情(后台调用)
     * @param channelId 渠道Id
     * @param bannerId bannerId
     * @return
     */
    @GetMapping("/detail")
    public BannerDto queryBannerDetail(@RequestParam Long id){
        return bannerService.queryBannerDetail(id, 0);
    }
}
