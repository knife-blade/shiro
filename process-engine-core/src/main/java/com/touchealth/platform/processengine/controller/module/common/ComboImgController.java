package com.touchealth.platform.processengine.controller.module.common;


import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDetailDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.ComboImgDetailRequest;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.module.common.ComboImgDetailService;
import com.touchealth.platform.processengine.service.module.common.ComboImgService;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import com.touchealth.platform.processengine.controller.BaseController;

import javax.annotation.Resource;

/**
 * <p>
 * 组合图通用组件表 前端控制器
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/comboImg")
public class ComboImgController extends BaseController {

    @Resource
    private ComboImgService comboImgService;

    @Resource
    private ComboImgDetailService comboImgDetailService;

    @GetMapping("/detail")
    public ComboImgDto getDetail(@RequestParam Long id){
        return comboImgService.findById(id,false);
    }

    @GetMapping("/list")
    public PageInfo<ComboImgDetailDto> list(@RequestHeader String channelNo, ComboImgDetailRequest request){
        request.setChannelNo(channelNo);
        return comboImgDetailService.queryList(request);
    }
}
