package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.entity.user.Department;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门数据
 */
@Data
@NoArgsConstructor
public class DepartmentDto {

    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 部门编码
     */
    private String departmentCode;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 包含人数
     */
    private Integer peopleCount;

    /**
     * 部门类型 0-saas平台 1-科助系统
     */
    private Integer type;
    /**
     * 岗位列表
     */
    private List<PostJobDto> postJobs;

    public DepartmentDto(Department department) {
        this.departmentId = department.getId();
        this.departmentCode = department.getCode();
        this.departmentName = department.getName();
        this.type = department.getType();
    }

}
