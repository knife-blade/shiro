package com.touchealth.platform.processengine.controller.channel;

import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.pojo.dto.page.ChannelConfigDto;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelConfigRequest;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 渠道配置接口
 *
 * @author liufengqiang
 * @date 2021-04-23 16:51:00
 */
@RestController
@RequestMapping("/platform-channel")
public class ChannelConfigController {

    @Resource
    private PlatformChannelService platformChannelService;

    /**
     * 新增渠道配置数据
     */
    @PostMapping("/{id}/config")
    @CacheEvict(value = RedisConstant.USER_BIND_CHANNEL_USER_LIST, allEntries = true)
    public void config(@PathVariable Long id, @RequestHeader String channelNo, @RequestBody @Valid PlatformChannelConfigRequest request) {
        platformChannelService.saveConfig(id, channelNo, request);
    }

    /**
     * 渠道配置数据列表
     *
     * @param dataType     数据类型 0.已选 1.新增
     * @param configType   渠道配置类型 0.医院数据 1.套餐数据 2.用户互通
     * @param displayRules 医院数据展示规则 0.全部展示 1.黑名单 2.白名单 和原展示规则不一样的话新增列表不用过滤
     */
    @GetMapping("/{id}/config")
    public ChannelConfigDto list(@RequestParam(defaultValue = "1") Integer pageNo,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @PathVariable Long id, @RequestParam Integer dataType,
                                 @RequestHeader String channelNo, @RequestParam Integer configType, Integer displayRules, String search) {
        PlatformChannel currentChannel = platformChannelService.getByChannelNo(channelNo);
        Assert.notNull(currentChannel, "当前渠道不存在");
        return platformChannelService.pageDataConfig(pageNo, pageSize, id, dataType, configType, displayRules, search, currentChannel.getId() == 1L);
    }

    /**
     * 删除渠道配置数据
     *
     * @param configType 渠道配置类型 0.医院数据 1.套餐数据 2.用户互通
     */
    @DeleteMapping("/{id}/config")
    @CacheEvict(value = RedisConstant.USER_BIND_CHANNEL_USER_LIST, allEntries = true)
    public void config(@PathVariable Long id, @RequestHeader String channelNo, @RequestParam Integer configType, @RequestParam Long dataId) {
        platformChannelService.deleteConfig(id, channelNo, configType, dataId);
    }
}
