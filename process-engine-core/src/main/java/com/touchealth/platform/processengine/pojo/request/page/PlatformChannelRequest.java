package com.touchealth.platform.processengine.pojo.request.page;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author liufengqiang
 * @date 2020-11-13 15:49:12
 */
@Data
public class PlatformChannelRequest {

    /** 父id */
    private Long parentId;
    /** 平台名称 */
    @NotBlank(message = "平台名称不能为空")
    @Size(max = 10, message = "字数不能超过10位")
    private String channelName;
    /** 渠道号 */
    private String channelNo;
    /** 平台logo */
    private String channelLogo;
    /** 合作业务 业务类型逗号拼接
     * @see PageCenterConsts.BusinessType */
    private String businessType;
    /** 上架状态 0.下架 1.上架 */
    private Integer shelfStatus;

    /** 管理员账号 */
    @NotBlank(message = "管理员账号不能为空")
    private String adminAccount;
    /** 超级管理员手机号 */
    @NotBlank(message = "超级管理员手机号不能为空")
    private String adminMobileNo;
    /** 超级管理员密码 */
    private String adminPassword;
    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
    /**
     * 一级渠道关联的租户id：platform_user.rent.id
     */
    private Long rentId;
}
