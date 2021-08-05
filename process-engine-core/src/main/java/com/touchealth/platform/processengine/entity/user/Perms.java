package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.constant.PermsConstant;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.touchealth.platform.processengine.constant.UserConstant;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Perms extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限资源编码
     * @see PermsConstant
     */
    private String code;

    /**
     * 权限资源名
     */
    private String name;

    /**
     * 权限资源类型。<br>
     * {@linkplain UserConstant#PERM_TYPE_MENU 0：菜单}；
     * {@linkplain UserConstant#PERM_TYPE_OP 1：功能}；
     * {@linkplain UserConstant#PERM_TYPE_DATA 2：数据}；
     * {@linkplain UserConstant#PERM_TYPE_FIELD 3：字段}；
     * {@linkplain UserConstant#PERM_TYPE_API 4：接口}
     */
    private Integer type;

    /**
     * 权限资源标识符。如：user:list、user:add
     * @see PermsConstant
     */
    private String permsCode;

    /**
     * 权限资源所属组
     */
    private String permsGroup;

    /**
     * 父权限资源ID
     */
    private Long pId;

    /**
     * 权限顺序
     */
    private Integer sort;

    /**
     * 权限资源所属组中的优先级
     */
    private Integer priority;


}
