package com.touchealth.platform.processengine.pojo.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class PermTemplatePostJobDto {

    private Long deptId;
    private Long postJobId;
    private String postJobName;
    private List<PermDto> permList;

}
