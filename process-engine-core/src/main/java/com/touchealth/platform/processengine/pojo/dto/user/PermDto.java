package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.constant.UserConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermDto {

    /**
     * 权限ID
     */
    private Long id;
    /**
     * 权限编码
     */
    private String code;
    /**
     * 权限资源标识符
     */
    private String permsCode;
    /**
     * 权限名
     */
    private String name;
    /**
     * 是否拥有权限
     */
    private Boolean have;
    /**
     * 权限组
     */
    private String group;
    /**
     * 权限资源类型。<br>
     * {@linkplain UserConstant#PERM_TYPE_MENU 0：菜单}；
     * {@linkplain UserConstant#PERM_TYPE_OP 1：功能}；
     * {@linkplain UserConstant#PERM_TYPE_DATA 2：数据}；
     * {@linkplain UserConstant#PERM_TYPE_FIELD 3：字段}；
     * {@linkplain UserConstant#PERM_TYPE_API 4：接口}
     */
    private Integer type;

}
