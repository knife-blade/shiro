package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.entity.user.Department;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门树数据
 */
@Data
@NoArgsConstructor
public class DepartmentTreeDto {

    /**
     * 部门ID
     */
    private Long id;
    /**
     * 部门编码
     */
    private String deptCod;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 包含人数
     */
    private int count;

    /**
     * 部门类型 0-saas平台 1-科助系统
     */
    private Integer type;
    /**
     * 岗位列表
     */
    private List<PostJobTreeDto> children;

    public DepartmentTreeDto(Department department) {
        this.id = department.getId();
        this.deptCod = department.getCode();
        this.deptName = department.getName();
    }

}
