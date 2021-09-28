package com.example.demo.rbac.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.rbac.role.entity.Role;

import java.util.Set;

public interface RoleService extends IService<Role> {
    Set<String> getRolesByUserId(String userId);
}
