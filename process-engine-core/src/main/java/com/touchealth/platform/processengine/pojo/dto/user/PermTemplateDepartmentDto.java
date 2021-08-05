package com.touchealth.platform.processengine.pojo.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class PermTemplateDepartmentDto {

    private Long deptId;
    private String deptName;
    private List<PermTemplatePostJobDto> postJobList;

}
