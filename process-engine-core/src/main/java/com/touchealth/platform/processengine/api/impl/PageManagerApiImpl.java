package com.touchealth.platform.processengine.api.impl;

import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.process.engine.api.PageManagerApi;
import com.touchealth.process.engine.dto.page.PageManagerDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liufengqiang
 * @date 2021-06-04 15:58:21
 */
@Service("pageManagerApi")
public class PageManagerApiImpl implements PageManagerApi {

    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PlatformChannelService platformChannelService;

    @Override
    public PageManagerDto getByRouterName(String routerName, String channelNo) {
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
        PageManagerDto pageManagerDto = BaseHelper.r2t(pageManagerService.getByRouterName(routerName, channelNo,
                platformChannel.getReleaseVersion()), PageManagerDto.class);
        pageManagerDto.setRentId(platformChannel.getRentId());
        return pageManagerDto;
    }
}
