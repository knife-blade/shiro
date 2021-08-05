package com.touchealth.platform.processengine.controller.user;

import com.touchealth.common.basic.response.PageInfo;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.pojo.dto.user.UserCenterDetailsDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserCenterListDto;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.user.client.api.UserApi;
import com.touchealth.platform.user.client.dto.UserDto;
import io.jsonwebtoken.lang.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户中心
 *
 * @author liufengqiang
 * @date 2021-04-10 11:26:40
 */
@RestController
@RequestMapping("/user-center")
public class UserCenterController {

    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private UserApi userApi;

    /**
     * 用户列表
     *
     * @param channelId 不传默认返回当前渠道用户
     */
    @GetMapping
    public PageInfo<UserCenterListDto> list(@RequestHeader String channelNo, Long channelId, String search, Integer pageNo, Integer pageSize) {
        PlatformChannel platformChannel;
        if (channelId != null) {
            platformChannel = platformChannelService.getById(channelId);
        } else {
            platformChannel = platformChannelService.getByChannelNo(channelNo);
        }
        Assert.notNull(platformChannel, "渠道不存在");
        Assert.notNull(platformChannel.getRentId(), "渠道租户未配置");

        return platformChannelService.pageUserByRentId(platformChannel.getRentId(), search, pageNo, pageSize);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public UserCenterDetailsDto details(@PathVariable Long id, @RequestHeader String channelNo, Long channelId) {
        PlatFormResponse<UserDto> response = userApi.findUserById(id);
        UserDto userDto = response.getDataOrThrowError();
        Assert.notNull(userDto, "用户不存在");

        UserCenterDetailsDto dto = BaseHelper.r2t(userDto, UserCenterDetailsDto.class);
        dto.setMobileNo(userDto.getMobile());
        dto.setAvatar(userDto.getHeadImg());
        return dto;
    }
}
