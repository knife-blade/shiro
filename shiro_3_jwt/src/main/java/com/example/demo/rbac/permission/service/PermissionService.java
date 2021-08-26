package com.example.demo.rbac.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.rbac.permission.entity.Permission;

import java.util.Set;

public interface PermissionService extends IService<Permission> {
    Set<String> getPermissionsByUserId(String userName);
}
