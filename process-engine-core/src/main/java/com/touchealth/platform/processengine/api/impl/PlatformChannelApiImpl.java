package com.touchealth.platform.processengine.api.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchealth.platform.basic.request.SearchPageRequest;
import com.touchealth.platform.basic.response.Pager;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.user.client.api.UserApi;
import com.touchealth.platform.user.client.dto.UserDto;
import com.touchealth.platform.user.client.dto.response.UserRes;
import com.touchealth.process.engine.api.PlatformChannelApi;
import com.touchealth.process.engine.dto.channel.ChannelBaseInfoDto;
import com.touchealth.process.engine.dto.channel.PlatformChannelDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liufengqiang
 * @date 2021-04-22 10:09:39
 */
@Service("platformChannelApi")
public class PlatformChannelApiImpl implements PlatformChannelApi {

    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private UserApi userApi;

    @Override
    public PlatformChannelDto getByChannelNo(String channelNo) {
        return BaseHelper.r2t(platformChannelService.getByChannelNo(channelNo), PlatformChannelDto.class);
    }

    @Override
    public List<ChannelBaseInfoDto> listBaseInfo() {
        return BaseHelper.r2t(platformChannelService.list(Wrappers.<PlatformChannel>lambdaQuery()
                .isNull(PlatformChannel::getParentId).orderByDesc(PlatformChannel::getCreatedTime)), ChannelBaseInfoDto.class);
    }

    @Override
    public Pager<PlatformChannelDto> pagePlatformChannelList(SearchPageRequest search) {
        Integer pageNo = search.getPageNo();
        Integer pageSize = search.getPageSize();
        String searchKey = search.getSearchKey();
        Pager<PlatformChannelDto> ret = new Pager<>(pageNo, pageSize);

        Page<PlatformChannel> page = platformChannelService.page(
                new Page<>(pageNo, pageSize),
                Wrappers.<PlatformChannel>lambdaQuery()
                        .like(PlatformChannel::getChannelName, searchKey)
                        .or()
                        .like(PlatformChannel::getChannelNo, searchKey)
                        .orderByDesc(PlatformChannel::getUpdatedTime));

        return wrapPlatformChannelPageInfo(ret, page);
    }

    @Override
    public Pager<PlatformChannelDto> pagePlatformChannelListNotIn(SearchPageRequest search, List<String> excludeNos) {
        if (CollectionUtils.isEmpty(excludeNos)) {
            return pagePlatformChannelList(search);
        }
        Integer pageNo = search.getPageNo();
        Integer pageSize = search.getPageSize();
        String searchKey = search.getSearchKey();
        Pager<PlatformChannelDto> ret = new Pager<>(pageNo, pageSize);

        Page<PlatformChannel> page = platformChannelService.page(
                new Page<>(pageNo, pageSize),
                Wrappers.<PlatformChannel>lambdaQuery()
                        .notIn(PlatformChannel::getChannelNo, excludeNos)
                        .and(i -> i.like(PlatformChannel::getChannelName, searchKey).or()
                                .like(PlatformChannel::getChannelNo, searchKey)
                        )
                        .orderByDesc(PlatformChannel::getUpdatedTime)
        );

        return wrapPlatformChannelPageInfo(ret, page);
    }

    /**
     * 包装渠道分页对象
     *
     * @param ret
     * @param page
     * @return
     */
    private Pager<PlatformChannelDto> wrapPlatformChannelPageInfo(Pager<PlatformChannelDto> ret, Page<PlatformChannel> page) {
        List<PlatformChannel> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            ret.setTotal((int) page.getPages());
            ret.setRecords((int) page.getTotal());
            List<PlatformChannelDto> channelDtos = records.stream().map(o -> BaseHelper.r2t(o, PlatformChannelDto.class)).collect(Collectors.toList());
            ret.setResultList(channelDtos);
        }

        return ret;
    }

    @Override
    public List<PlatformChannelDto> listPlatformChannel(List<String> channelNos) {
        if (CollectionUtils.isEmpty(channelNos)) {
            return new ArrayList<>();
        }
        List<PlatformChannel> channels = platformChannelService.list(Wrappers.<PlatformChannel>lambdaQuery()
                .in(PlatformChannel::getChannelNo, channelNos)
                .orderByDesc(PlatformChannel::getUpdatedTime));
        if (CollectionUtils.isNotEmpty(channels)) {
            return channels.stream().map(o -> BaseHelper.r2t(o, PlatformChannelDto.class)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public PlatformChannelDto getByUserId(Long userId) {
        PlatFormResponse<UserDto> userDto = userApi.findUserById(userId);
        Assert.notNull(userDto.getData(), "用户不存在");

        PlatformChannel platformChannel = platformChannelService.getOne(Wrappers.<PlatformChannel>lambdaQuery()
                .eq(PlatformChannel::getRentId, userDto.getData().getRentId()));
        return BaseHelper.r2t(platformChannel, PlatformChannelDto.class);
    }

    @Override
    @Cacheable(value = RedisConstant.USER_BIND_CHANNEL_USER_LIST, key = "#userId")
    public List<Long> listBindUserIds(Long userId) {
        PlatFormResponse<UserDto> userDto = userApi.findUserById(userId);
        Assert.notNull(userDto.getData(), "用户不存在");

        PlatformChannel platformChannel = platformChannelService.getByRentId(userDto.getData().getRentId());
        if (platformChannel != null && StringUtils.isNotBlank(platformChannel.getBindChannelNos())) {
            List<PlatformChannel> platformChannels = platformChannelService.list(Wrappers.<PlatformChannel>lambdaQuery()
                    .in(PlatformChannel::getChannelNo, Arrays.asList(platformChannel.getBindChannelNos().split(","))));
            List<Long> rentIds = platformChannels.stream().map(PlatformChannel::getRentId).collect(Collectors.toList());
            PlatFormResponse<List<UserRes>> userRes = userApi.findUser(userDto.getData().getMobile(), "mobile", rentIds);
            if (CollectionUtils.isNotEmpty(userRes.getDataOrThrowError())) {
                return userRes.getDataOrThrowError().stream().map(UserRes::getId).collect(Collectors.toList());
            }
        }
        return Collections.singletonList(userId);
    }

    @Override
    public Set<Long> listBindByRentId(Long rentId) {
        Set<Long> rentIds = new HashSet<>(Collections.singleton(rentId));

        PlatformChannel platformChannel = platformChannelService.getByRentId(rentId);
        if (platformChannel != null && StringUtils.isNotBlank(platformChannel.getBindChannelNos())) {
            List<PlatformChannel> platformChannels = platformChannelService.listByIds(Arrays.asList(platformChannel.getBindChannelNos().split(",")));
            rentIds.addAll(platformChannels.stream().map(PlatformChannel::getRentId).collect(Collectors.toSet()));
        }
        return rentIds;
    }

    @Override
    public List<PlatformChannelDto> listByRentIds(Collection<Long> rentIds) {
        return BaseHelper.r2t(platformChannelService.list(Wrappers.<PlatformChannel>lambdaQuery().in(PlatformChannel::getRentId, rentIds)), PlatformChannelDto.class);
    }
}
