package com.example.demo.business.rbac.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.business.rbac.role.entity.Role;

import java.util.Set;

public interface RoleService extends IService<Role> {
    Set<String> getRolesByUserName(String userName);
}
