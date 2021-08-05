package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户权限关系表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserPerms extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所在的渠道编码
     */
    private String channelNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限资源ID
     */
    private Long permsId;

    /**
     * 权限资源类型。<br>
     * {@linkplain UserConstant#PERM_TYPE_MENU 0：菜单}；
     * {@linkplain UserConstant#PERM_TYPE_OP 1：功能}；
     * {@linkplain UserConstant#PERM_TYPE_DATA 2：数据}；
     * {@linkplain UserConstant#PERM_TYPE_FIELD 3：字段}；
     * {@linkplain UserConstant#PERM_TYPE_API 4：接口}
     */
    private Integer permsType;

    public UserPerms(String channelNo, Long userId, Long permsId, Integer permsType) {
        this.channelNo = channelNo;
        this.userId = userId;
        this.permsId = permsId;
        this.permsType = permsType;
    }
}
