package com.example.demo.business.rbac.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_permission")
public class Permission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

}