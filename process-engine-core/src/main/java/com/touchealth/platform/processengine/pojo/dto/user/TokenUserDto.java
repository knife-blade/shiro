package com.touchealth.platform.processengine.pojo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchealth.platform.processengine.constant.UserConstant;
import lombok.Data;

import java.util.Date;

@Data
public class TokenUserDto {

    /**
     * 主键
     */
    private Long id;
    /**
     * 编码
     */
    private String code;
    /**
     * 用户类型<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE 10-流程引擎平台用户}<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE_ADMIN 11-流程引擎平台管理员}
     */
    private Integer userType;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 性别：男、女
     */
    private String sex;
    /**
     * 婚姻情况：已婚、未婚
     */
    private String maritalStatus;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobileNo;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
}
