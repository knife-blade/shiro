package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 员工部门表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 员工部门所在的渠道编码
     */
    private String channelNo;

    /**
     * 部门编码。预留字段，暂时用不到
     */
    private String code;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门类型 0-saas平台部门 1-科助系统部门
     */
    private Integer type;

    public Department(String channelNo, String code, String name) {
        this.channelNo = channelNo;
        this.code = code;
        this.name = name;
    }

}
