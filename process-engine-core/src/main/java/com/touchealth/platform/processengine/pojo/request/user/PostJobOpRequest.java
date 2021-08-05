package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.constant.ValidGroup;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class PostJobOpRequest {

    @NotNull(groups = ValidGroup.Edit.class, message = "岗位ID不能为空")
    private Long id;
    @NotEmpty(message = "岗位名不能为空")
    private String name;
    @NotNull(message = "部门ID不能为空")
    private Long deptId;
    private String channelNo;

}

