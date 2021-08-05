package com.touchealth.platform.processengine.controller.module.common;


import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.module.common.NavigateImgService;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import com.touchealth.platform.processengine.controller.BaseController;

import javax.annotation.Resource;

/**
 * <p>
 * 坑位导航通用组件表之图片内容表 前端控制器
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/navigateImg")
public class NavigateImgController extends BaseController {

    @Resource
    private NavigateImgService navigateImgService;

    @GetMapping("/list")
    public PageInfo<NavigateImgDto> list(@RequestHeader String channelNo, NavigateRequest request){
        request.setChannelNo(channelNo);
        return navigateImgService.queryList(request);
    }
}
