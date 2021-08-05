package com.touchealth.platform.processengine.service.page;

import com.touchealth.common.basic.response.PageInfo;
import com.touchealth.common.page.Pager;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.pojo.dto.page.ChannelConfigDto;
import com.touchealth.platform.processengine.pojo.dto.platformchannel.RentDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserCenterListDto;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelConfigRequest;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelRequest;
import com.touchealth.platform.processengine.pojo.request.page.PresetPageRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 平台渠道表 服务类
 * </p>
 *
 * @author admin
 * @since 2020-11-16
 */
public interface PlatformChannelService extends BaseService<PlatformChannel> {

    /**
     * 新增渠道
     *
     * @param userId
     * @param request
     * @return
     */
    PlatformChannel saveChannel(Long userId, PlatformChannelRequest request);

    /**
     * 删除渠道
     *
     * @param id
     */
    void deleteChannel(Long id);

    /**
     * 更新渠道
     *
     * @param userId
     * @param platformChannel
     * @param request
     */
    void updateChannel(Long userId, PlatformChannel platformChannel, PlatformChannelRequest request);

    /**
     * 获取渠道号
     *
     * @param channelNo
     * @return
     */
    PlatformChannel getByChannelNo(String channelNo);

    /**
     * 获取生产版本号
     *
     * @param channelNo
     * @return
     */
    Long getReleaseVersionIdByChannelNo(String channelNo);

    /**
     * 查询平台列表
     *
     * @param channelName
     * @return
     */
    List<PlatformChannel> listByChannelName(String channelName);

    /**
     * 根据渠道号获取对应登录页ID
     *
     * @param channelNo 渠道code
     * @return ID
     */
    String getLoginPageIdByChannelNo(String channelNo);

    /**
     * 根据管理员账号查询数据
     *
     * @param adminAccount
     * @return
     */
    PlatformChannel getByAdminAccount(String adminAccount);

    /**
     * 根据渠道号列表查询
     *
     * @param channelNos
     * @return
     */
    List<PlatformChannel> listByChannelNos(List<String> channelNos);

    /**
     * 根据租户id查询渠道
     *
     * @param rentId
     * @return
     */
    PlatformChannel getByRentId(Long rentId);

    /**
     * 查询租户对应的用户分页列表
     *
     * @param rentId
     * @param search
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<UserCenterListDto> pageUserByRentId(Long rentId, String search, Integer pageNo, Integer pageSize);

    /**
     * 新增渠道预置页面
     *
     * @param userId
     * @param request
     */
    void addPresetPage(Long userId, List<PresetPageRequest> request);

    /**
     * 渠道配置数据列表
     *
     * @param pageNo
     * @param pageSize
     * @param id
     * @param dataType
     * @param configType
     * @param displayRules
     * @param search
     * @return
     */
    ChannelConfigDto pageDataConfig(Integer pageNo, Integer pageSize, Long id, Integer dataType, Integer configType, Integer displayRules, String search, Boolean isMain);

    /**
     * 未关联渠道的租户列表
     */
    Pager<RentDto> unusedRentList(Pager pager);

    /**
     * 新增渠道配置数据
     *
     * @param id
     * @param channelNo
     * @param request
     */
    void saveConfig(Long id, String channelNo, PlatformChannelConfigRequest request);

    /**
     * 删除渠道配置数据
     *
     * @param id
     * @param channelNo
     * @param configType
     * @param dataId
     */
    void deleteConfig(Long id, String channelNo, Integer configType, Long dataId);
}
