package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.constant.UserConstant;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 编码
     */
    private String code;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobileNo;
    /**
     * 用户类型<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE 10-流程引擎平台用户}<br>
     * {@linkplain UserConstant#USER_TYPE_PROCESS_ENGINE_ADMIN 11-流程引擎平台管理员}
     */
    private Integer userType;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 岗位ID
     */
    private Long postJobId;
    /**
     * 部门名
     */
    private String deptName;
    /**
     * 岗位名
     */
    private String postJobName;
    /**
     * 员工状态。
     * {@linkplain UserConstant#STAFF_STATUS_IN 0：在职}；
     * {@linkplain UserConstant#STAFF_STATUS_OUT 1：离职}；
     */
    private Integer staffStatus;
    /**
     * 渠道编码
     */
    private String channelNo;
    /**
     * 是否禁用 0-正常 非0-禁用
     * @see UserConstant#USE_ENABLE
     * @see UserConstant#USE_DISABLE
     */
    private Integer isDisable;
    /**
     * 是否删除 0-正常 非0-删除
     */
    private Long isDel;
    /**
     * 注册时间
     */
    private Date createdAt;
    /**
     * 创建人
     */
    private Long createdBy;
    /**
     * 更新时间
     */
    private Date updatedAt;
    /**
     * 更新人
     */
    private Long updatedBy;


}
