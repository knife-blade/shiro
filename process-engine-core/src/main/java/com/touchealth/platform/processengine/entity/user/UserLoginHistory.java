package com.touchealth.platform.processengine.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.touchealth.platform.processengine.constant.UserConstant;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

@Data
@TableName("user_login_history")
public class UserLoginHistory implements Serializable {
    private static final long serialVersionUID = 948841249830556204L;
    /**
    * 主键
    */
    private Long id;
    /**
     * 用户类型<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE_H5 5-流程引擎前端用户}
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE 10-流程引擎平台用户}<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE_ADMIN 11-流程引擎平台管理员}
     */
    private Integer userType;
    /**
    * user.id
    */
    private Long userId;
    /**
    * 登录时间
    */
    private Date loginTime;
    /**
    * 登录用户的唯一标识，该标识会存储在登录的token中
    */
    private String currentUserRemark;
    /**
    * 登录地理位置
    */
    private String loginArea;
    /**
    * IP地址
    */
    private String ipAddress;
    /**
    * Header中的referer
    */
    private String referer;
    /**
    * 登录状态：1-登录中 2-已登出
    */
    private Integer loginStatus;
    /**
    * 登出时间
    */
    private Date logoutTime;
    /**
    * 是否失效：0-正常 1-失效
    */
    private Integer isDisable;
    /**
    * 是否删除
    */
    private Long isDeleted;
    /**
    * 乐观锁
    */
    @Version
    private Integer version;

    /**
     * 过期失效时间
     */
    private Date expirationTime;

}