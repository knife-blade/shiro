package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.pojo.request.PageDataRequest;
import lombok.Data;

@Data
public class UserSearchRequest extends PageDataRequest {

    /**
     * 搜索字段
     */
    private String search;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 岗位ID
     */
    private Long postJobId;
    /**
     * 渠道编码
     */
    private String channelNo;
    /**
     * 员工状态。
     * {@linkplain UserConstant#STAFF_STATUS_IN 0：在职}；
     * {@linkplain UserConstant#STAFF_STATUS_OUT 1：离职}；
     */
    private Integer staffStatus;

    /**
     * 类型 0:sass平台员工 1:运营-科助中心员工
     */
    private Integer type;
}