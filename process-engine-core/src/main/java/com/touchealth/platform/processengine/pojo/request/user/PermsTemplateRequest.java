package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PermsTemplateRequest {

    /**
     * 岗位ID
     */
    @NotNull(message = "岗位ID不能为空")
    private Long postJobId;
    /**
     * 权限列表
     */
    private List<PermDto> permList;

}
