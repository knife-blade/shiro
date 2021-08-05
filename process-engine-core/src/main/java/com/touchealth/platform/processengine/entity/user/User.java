package com.touchealth.platform.processengine.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.touchealth.platform.processengine.constant.UserConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user")
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 417113959212232872L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 编码
     */
    @TableField(value = "`code`")
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
    private String mobileNo;
    /**
     * 密码
     */
    @TableField(value = "`password`")
    private String password;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 证件类型 0-身份证 1-护照 2-军官证 3-医保卡 4-居住证 5-驾驶证
     */
    private Integer identityType;
    /**
     * 证件号
     */
    private String identityNo;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 地区
     */
    @TableField(value = "`distinct`")
    private String distinct;
    /**
     * 备注
     */
    private String remark;
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
     * 出生日期
     */
    private Date birthday;
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
    @TableLogic(delval = "id")
    @TableField(select = false)
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
    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    /**
     * 关联用户中台的userId
     */
    private Long platformUserId;

    public User(String code, String realName, String email, String mobileNo, String password, String salt, Long deptId,
                Long postJobId, Integer staffStatus, String channelNo, Integer userType, Integer isDisable, Long isDel, Integer version) {
        this.code = code;
        this.realName = realName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.salt = salt;
        this.deptId = deptId;
        this.postJobId = postJobId;
        this.staffStatus = staffStatus;
        this.channelNo = channelNo;
        this.userType = userType;
        this.isDisable = isDisable;
        this.isDel = isDel;
        this.version = version;
    }
}
