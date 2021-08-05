package com.touchealth.platform.processengine.controller.module.common;


import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotPartsDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HotspotRequest;
import com.touchealth.platform.processengine.service.module.common.HotspotPartsService;
import com.touchealth.platform.processengine.service.module.common.HotspotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import com.touchealth.platform.processengine.controller.BaseController;

/**
 * <p>
 * 热区组件表 前端控制器
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
@RestController
@RequestMapping("/hotspot")
public class HotspotController extends BaseController {

    @Autowired
    private HotspotService hotspotService;

    @Autowired
    private HotspotPartsService hotspotPartsService;

    @GetMapping("/detail")
    public HotspotDto queryDetail(@RequestParam Long id){
        return hotspotService.findById(id,false);
}

    @GetMapping("/list")
    public PageInfo<HotspotPartsDto> getList(@RequestHeader String channelNo, HotspotRequest request){
        request.setChannelNo(channelNo);
        return hotspotPartsService.getList(request);
    }
}
