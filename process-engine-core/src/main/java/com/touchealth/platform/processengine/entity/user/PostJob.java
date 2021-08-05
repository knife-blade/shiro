package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 员工岗位表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PostJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 员工岗位所在的渠道编码
     */
    private String channelNo;

    /**
     * 岗位编码。预留字段，暂时用不到
     */
    private String code;

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 部门ID
     */
    private Long deptId;

    public PostJob(String channelNo, String code, String name, Long deptId) {
        this.channelNo = channelNo;
        this.code = code;
        this.name = name;
        this.deptId = deptId;
    }
}
