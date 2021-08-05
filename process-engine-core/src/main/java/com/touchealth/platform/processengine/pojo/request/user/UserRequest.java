package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.constant.ValidGroup;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class UserRequest {

    @NotNull(groups = ValidGroup.Edit.class, message = "用户ID不能为空")
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
    @NotEmpty(message = "手机号不能为空")
    private String mobileNo;
    /**
     * 密码
     */
    @NotEmpty(message = "密码号不能为空")
    private String password;
    /**
     * 密码
     */
    private String password1;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 岗位ID
     */
    private Long postJobId;
    /**
     * 员工状态。
     * {@linkplain UserConstant#STAFF_STATUS_IN 0：在职}；
     * {@linkplain UserConstant#STAFF_STATUS_OUT 1：离职}；
     */
    private Integer staffStatus;

    private String verifyCode;

    /**
     * 是否是科助系统添加员工信息
     */
    private Boolean isKeZhu;
}