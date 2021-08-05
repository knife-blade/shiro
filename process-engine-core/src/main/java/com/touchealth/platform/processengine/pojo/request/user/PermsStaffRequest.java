package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PermsStaffRequest {

    /**
     * 员工ID
     */
    private Long staffId;
    /**
     * 岗位ID列表（模板）
     */
    private List<Long> postJobIds;
    /**
     * 渠道编码
     */
    private String channelNo;
    /**
     * 权限列表
     */
    private List<PermDto> permList;

}
