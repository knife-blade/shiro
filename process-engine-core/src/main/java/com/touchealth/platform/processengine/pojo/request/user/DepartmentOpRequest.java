package com.touchealth.platform.processengine.pojo.request.user;

import com.touchealth.platform.processengine.constant.ValidGroup;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class DepartmentOpRequest {

    @NotNull(groups = ValidGroup.Edit.class, message = "部门ID不能为空")
    private Long id;
    @NotEmpty(message = "部门名不能为空")
    private String name;
    private String channelNo;
    /**
     * 部门类型 0-saas平台部门 1-科助系统部门
     */
    private Integer type;
}
