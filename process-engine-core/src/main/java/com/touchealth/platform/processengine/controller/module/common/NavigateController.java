package com.touchealth.platform.processengine.controller.module.common;


import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateDto;
import com.touchealth.platform.processengine.service.module.common.NavigateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import com.touchealth.platform.processengine.controller.BaseController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 坑位导航组件表 前端控制器
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/navigate")
public class NavigateController extends BaseController {

    @Resource
    private NavigateService navigateService;

    @GetMapping("/detail")
    public NavigateDto getDetail(@RequestParam Long id){
        return navigateService.findById(id,false);
    }
}
