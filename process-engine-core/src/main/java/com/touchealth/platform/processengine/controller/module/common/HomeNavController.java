package com.touchealth.platform.processengine.controller.module.common;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.controller.page.PageManagerController;
import com.touchealth.platform.processengine.entity.module.common.HomeNav;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.HomeNavDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HomeNavRequest;
import com.touchealth.platform.processengine.service.module.common.HomeNavService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.ROUTER_NAME_HOME;

/**
 * <p>
 * 首页导航组件 前端控制器
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/homeNav")
public class HomeNavController extends BaseController {

    @Resource
    private HomeNavService homeNavService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PageManagerController pmController;

    /**
     * 获取渠道某个版本对应的首页导航组件信息
     * @param channelNo 渠道号
     * @param versionId 版本号
     * @return 首页导航组件信息
     */
    @GetMapping("/query")
    public Response getHomeNav(@RequestHeader String channelNo, Long versionId){
        Assert.notNull(channelNo,"渠道号不能为空");
        HomeNav navQuery =  new HomeNav();
        navQuery.setChannelNo(channelNo);
        navQuery.setVersion(versionId);
        HomeNavDto homeNavDto = homeNavService.queryHomeNave(navQuery);
        Assert.notNull(homeNavDto,"首页导航组件信息不存在");
        return Response.ok(homeNavDto);
    }

    /**
     * 修改首页组件导航对应的信息
     * @param homeNavId 首页导航组件id
     * @param homeNavRequest 配置信息
     * @return 更新状态
     */
    @PutMapping("/{homeNavId}")
    public Response updateHomeNav(@PathVariable Long homeNavId,@RequestBody HomeNavRequest homeNavRequest,
        @RequestAttribute Long userId){
        Assert.notNull(homeNavRequest.getWebJson(),"配置信息不能为空");
        HomeNav homeNav = homeNavService.getById(homeNavId);
        Assert.notNull(homeNav,"组件信息不存在");
        QueryWrapper<PageManager> queryWrapper = new QueryWrapper<>(new PageManager());
        queryWrapper.getEntity().setRouterName(ROUTER_NAME_HOME);
        queryWrapper.getEntity().setChannelNo(homeNav.getChannelNo());
        queryWrapper.getEntity().setVersionId(homeNav.getVersion());
        PageManager homePage = pageManagerService.getOne(queryWrapper);
        Assert.notNull(homePage,"无当前页面编辑权限");
        pmController.pageVerify(userId,homeNav.getChannelNo(),homePage.getId());
        //保存更新后的信息
        WebJsonBo webJsonBo = JSONObject.parseObject(homeNavRequest.getWebJson(), WebJsonBo.class);
        WebJsonBo alterWebJsonBo = homeNavService.updateWebJson(webJsonBo, null);
        return Response.ok(alterWebJsonBo);
    }

}
