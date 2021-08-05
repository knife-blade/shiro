package com.touchealth.platform.processengine.controllermobile;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.module.common.HomeNav;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.HomeNavDto;
import com.touchealth.platform.processengine.service.module.common.HomeNavService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.ENV_TYPE_PREVIEW;
import static com.touchealth.platform.processengine.constant.PageCenterConsts.ENV_TYPE_RELEASE;

/**
 * <p>
 * 首页导航组件 前端控制器
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/mobile/homeNav")
public class MobileHomeNavController extends BaseController {

    @Resource
    private HomeNavService homeNavService;

    @Resource
    private PlatformChannelService platformChannelService;

    /**
     * 获取渠道某个版本对应的首页导航组件信息
     * @param channelNo 渠道号
     * @param envType 环境类型 0.线上 1.预览
     * @param versionId 版本号
     * @return 首页导航组件信息
     */
    @PassToken
    @GetMapping("/query")
    public Response getHomeNav(@RequestHeader String channelNo, @RequestHeader String envType,Long versionId){
        Assert.notNull(channelNo,"渠道号不能为空");
        switch (envType) {
            case ENV_TYPE_RELEASE:
                PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
                Assert.notNull(platformChannel, "平台不存在");
                versionId = platformChannel.getReleaseVersion();
                break;
            case ENV_TYPE_PREVIEW:
                Assert.notNull(versionId,"versionId不能为空");
                break;
            default:
                throw new BusinessException("环境类型异常");
        }
        HomeNav navQuery =  new HomeNav();
        navQuery.setVersion(versionId);
        navQuery.setChannelNo(channelNo);
        HomeNavDto homeNavDto = homeNavService.queryHomeNave(navQuery);
        Assert.notNull(homeNavDto,"首页导航组件信息不存在");
        return Response.ok(homeNavDto);
    }
}
