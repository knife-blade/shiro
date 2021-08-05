package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 平台渠道表
 * </p>
 *
 * @author admin
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("platform_channel")
public class PlatformChannel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道号
     */
    private String channelNo;
    /**
     * 上级渠道id 为空表示为顶级渠道
     */
    private Long parentId;
    /**
     * 平台名称
     */
    private String channelName;
    /**
     * 平台logo
     */
    private String channelLogo;
    /**
     * 合作业务 业务类型逗号拼接
     *
     * @see PageCenterConsts.BusinessType
     */
    private String businessType;
    /**
     * 管理员 ,号分隔
     */
    private String administrator;
    /**
     * 上架状态 0.下架 1.上架
     */
    private Integer shelfStatus;
    /**
     * 生产版本
     */
    private Long releaseVersion;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 层级目录
     */
    private String levelIndex;
    /**
     * 超级管理员
     */
    private Long adminId;
    /**
     * 超级管理员手机号
     */
    private String adminMobileNo;
    /**
     * 超级管理员账号
     */
    private String adminAccount;
    /**
     * 绑定医院
     */
    private Long hospitalId;

    /**
     * 租户id
     */
    private Long rentId;

    /**
     * 势成云允许展示规则 医院 0.全部展示 1.黑名单 2.白名单
     */
    private Integer hospitalPermitRules;
    /**
     * 势成云允许展示列表 医院
     */
    private String hospitalPermitNos;
    /**
     * 数据展示规则 医院 0.全部展示 1.黑名单 2.白名单
     */
    private Integer hospitalDisplayRules;
    /**
     * 数据列表 医院
     */
    private String hospitalNos;

    /**
     * 势成云允许展示规则 套餐 0.全部展示 1.黑名单 2.白名单
     */
    private Integer setMealPermitRules;
    /**
     * 势成云允许展示列表 套餐
     */
    private String setMealPermitNos;
    /**
     * 数据展示规则 套餐 0.全部展示 1.黑名单 2.白名单
     */
    private Integer setMealDisplayRules;
    /**
     * 数据列表 套餐
     */
    private String setMealNos;

    /**
     * 互通渠道列表
     */
    private String bindChannelNos;
}
