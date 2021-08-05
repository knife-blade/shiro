package com.touchealth.platform.processengine.controller.page;


import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.page.PageTemplateDto;
import com.touchealth.platform.processengine.service.module.common.PageTemplateService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 页面配置模板 前端控制器
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/pageTemplate")
public class PageTemplateController extends BaseController {

    @Resource
    private PageTemplateService pageTemplateService;

    /**
     * 获取所有的页面模板
     * @return 页面模板列表
     */
    @GetMapping("/templates")
    public Response getPageTemplates(){
        List<PageTemplate> list = pageTemplateService.list();
        return Response.ok(BaseHelper.r2t(list,PageTemplateDto.class));
    }
}
