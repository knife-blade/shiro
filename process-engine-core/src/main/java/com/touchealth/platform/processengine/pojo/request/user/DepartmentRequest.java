package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.pojo.request.PageDataRequest;
import lombok.Data;

@Data
public class DepartmentRequest extends PageDataRequest {

    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 部门编码
     */
    private String departmentCode;
    /**
     * 部门名
     */
    private String departmentName;
    /**
     * 岗位ID
     */
    private Long postJobId;
    /**
     * 岗位编码
     */
    private String postJobCode;
    /**
     * 岗位名
     */
    private String postJobName;

    /**
     * 渠道编码
     */
    private String channelNo;

    /**
     * 部门类型 0-saas平台部门 1-科助系统部门
     */
    private Integer type;

}
