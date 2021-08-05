package com.touchealth.platform.processengine.pojo.dto.channel;

import com.touchealth.platform.processengine.pojo.dto.page.BusinessTypeDto;
import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-16 17:35:29
 */
@Data
public class PlatformChannelDto {

    private Long id;
    /** 渠道号 */
    private String channelNo;
    /** 上级平台id */
    private Long parentId;
    /** 上级平台名称 */
    private String parentName;
    /** 渠道名 */
    private String channelName;
    /** 平台logo */
    private String channelLogo;
    /** 下属渠道数 **/
    private Integer childChannelNum = 0;
    /** 合作业务 */
    private List<BusinessTypeDto> businessType;
    /** 上架状态 0.下架 1.上架 */
    private Integer shelfStatus;
    /** 是否有权限 */
    private Boolean hasPerm;
    /** 添加用户时所在的渠道，用户的自身渠道，而非后期绑定的 */
    private Boolean oneself;
    /** 子渠道列表 */
    private List<PlatformChannelDto> children;

    /** 是否能切换 */
    private Boolean isSwitch;
    /** 超级管理员 */
    private Long adminId;
    /** 超级管理员手机号 */
    private String adminMobileNo;
    /** 超级管理员账号 */
    private String adminAccount;
    /**
     * 租户id
     */
    private Long rentId;
    /**
     * 租户名称
     */
    private String rentName;
}
