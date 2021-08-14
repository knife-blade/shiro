package com.example.demo.business.rbac.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.business.rbac.permission.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService extends IService<Permission> {
    Set<String> getPermissionsByUserName(String userName);
}
