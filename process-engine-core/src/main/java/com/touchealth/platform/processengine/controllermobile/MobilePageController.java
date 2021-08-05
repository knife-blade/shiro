package com.touchealth.platform.processengine.controllermobile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.page.PageDetailsDto;
import com.touchealth.platform.processengine.pojo.dto.page.PageManagerDto;
import com.touchealth.platform.processengine.pojo.dto.page.PageModuleDto;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageModuleService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.PageCenterConsts.ENV_TYPE_PREVIEW;
import static com.touchealth.platform.processengine.constant.PageCenterConsts.ENV_TYPE_RELEASE;
import static com.touchealth.platform.processengine.constant.RedisConstant.PageCenter.getPagePreviewKey;

/**
 * @author liufengqiang
 * @date 2021-01-15 14:36:26
 */
@RestController
@RequestMapping("/mobile/page")
public class MobilePageController {

    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PlatformVersionService platformVersionService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private RedisService redisService;
    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private ModuleService moduleService;

    /**
     * 页面列表【分页】
     *
     * @param versionId  版本号 没传默认为最新草稿版本
     * @param queryType  查询类型 0/空.默认 1.仅显示改动项
     * @param moduleType 组件类型
     */
    @GetMapping
    @PassToken
    public Response list(@RequestHeader String channelNo, @RequestParam(defaultValue = "0") Integer queryType,
                         Long versionId, Integer businessType, Integer moduleType) {
        return Response.ok(pageManagerService.pageList(channelNo, queryType, versionId, businessType, moduleType));
    }

    /**
     * 页面列表
     */
    @GetMapping("/list")
    @PassToken
    public List<PageManagerDto> list(@RequestHeader String channelNo, @RequestParam(required = false) Long versionId) {
        if (versionId == null) {
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
            Assert.notNull(platformChannel, "渠道不存在");
            versionId = platformChannel.getReleaseVersion();
        }

        LambdaQueryWrapper<PageManager> qw = Wrappers.<PageManager>lambdaQuery().eq(PageManager::getChannelNo, channelNo);
        if (versionId != null) {
            qw.eq(PageManager::getVersionId, versionId);
        }
        List<PageManager> list = pageManagerService.list(qw);
        return list.stream().map(o -> {
            PageManagerDto dto = BaseHelper.r2t(o, PageManagerDto.class);
            dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(o.getBusinessType()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 页面详情
     * 下面接口是通过id获取页面详情，这个接口是通过非id获取，比如：通过routerName
     */
    @GetMapping("/details")
    @PassToken
    public Response pageDetails(@RequestHeader String channelNo, @RequestParam String routerName, @RequestHeader String envType, Long versionId) {
        switch (envType) {
            case ENV_TYPE_RELEASE:
                PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
                Assert.notNull(platformChannel, "平台不存在");
                versionId = platformChannel.getReleaseVersion();
                break;
            case ENV_TYPE_PREVIEW:
                versionId = platformVersionService.getDefaultVersionId(channelNo, versionId);
                break;
            default:
                throw new BusinessException("环境类型异常");
        }

        // 正常只有一个页面，万一有多个取第一个
        PageManager pageManager = pageManagerService.getOne(Wrappers.<PageManager>lambdaQuery()
                .eq(PageManager::getRouterName, routerName)
                .eq(PageManager::getVersionId, versionId)
                .last("limit 1"));
        if (pageManager == null) {
            return Response.success;
        }

        PageDetailsDto dto = BaseHelper.r2t(pageManager, PageDetailsDto.class);
        dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(pageManager.getBusinessType()));
        dto.setRouterName(pageManager.getRouterName());
        return Response.ok(dto);
    }

    /**
     * 页面详情
     *
     * @param envType 环境类型 0.线上 1.预览
     */
    @GetMapping("/{pageUniqueId}")
    @PassToken
    public Response details(@RequestHeader String channelNo, @RequestHeader String envType, @PathVariable String pageUniqueId, Long versionId) {
        PageDetailsDto dto = new PageDetailsDto();

        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
        Assert.notNull(platformChannel, "平台不存在");
        dto.setChannelName(platformChannel.getChannelName());

        PlatformVersion platformVersion = null;
        switch (envType) {
            case ENV_TYPE_RELEASE:
                versionId = platformChannel.getReleaseVersion();
                break;
            case ENV_TYPE_PREVIEW:
                platformVersion = platformVersionService.getDefaultVersion(channelNo, versionId);
                break;
            default:
                throw new BusinessException("环境类型异常");
        }
        if (platformVersion == null) {
            platformVersion = platformVersionService.getById(versionId);
        }
        Assert.notNull(platformVersion, "版本号不存在");

        PageManager pageManager = pageManagerService.getByPageUniqueId(platformVersion.getId(), pageUniqueId);
        Assert.notNull(pageManager, "页面不存在");

        if (CommonConstant.STATUS.TRASH.getCode().equals(pageManager.getStatus())) {
            dto.setExceptionStatus(2);
        } else if (ENV_TYPE_RELEASE.equals(envType)) {
            if (platformChannel.getShelfStatus() == 0) {
                dto.setExceptionStatus(1);
            }
        } else {
            Boolean isValid = redisService.getValue(getPagePreviewKey(channelNo, platformVersion.getId()));
            if (isValid == null) {
                dto.setExceptionStatus(0);
            }
        }

        if (dto.getExceptionStatus() != null) {
            return Response.ok(dto);
        }

        dto.setBusinessType(PageCenterConsts.BusinessType.getNameByCode(pageManager.getBusinessType()));
        dto.setRouterName(pageManager.getRouterName());
        dto.setPageUniqueId(pageManager.getPageUniqueId());
        dto.setPageName(pageManager.getPageName());

        List<PageModule> pageModules = pageModuleService.listByPageId(pageManager.getId());
        dto.setModules(pageModules.stream().map(o -> {
            PageModuleDto pageModuleDto = new PageModuleDto();
            pageModuleDto.setId(o.getId());
            pageModuleDto.setModuleType(o.getModuleType());

            if (CommonConstant.ModuleType.CAROUSEL.getCode().equals(o.getModuleType())) {
                pageModuleDto.setWebJson(JSON.parseObject(moduleService.getInstance(o.getModuleType()).getModuleById(o.getModuleId(), envType)));
            } else {
                pageModuleDto.setWebJson(JSON.parseObject(o.getWebJson()));
            }
            return pageModuleDto;
        }).collect(Collectors.toList()));
        return Response.ok(dto);
    }


    /**
     * 操作日志
     */
    @GetMapping("/operation-log")
    @PassToken
    public Response operationLog(@RequestHeader String channelNo, Long versionId) {
        return Response.ok(platformVersionService.operationLog(channelNo, versionId));
    }
}
