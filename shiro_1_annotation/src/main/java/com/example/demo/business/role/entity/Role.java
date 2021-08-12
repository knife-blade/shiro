package com.example.demo.business.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_role")
public class Role {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

}