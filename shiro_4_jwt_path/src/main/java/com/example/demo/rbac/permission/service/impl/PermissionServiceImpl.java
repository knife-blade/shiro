package com.example.demo.rbac.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.rbac.permission.entity.Permission;
import com.example.demo.rbac.permission.mapper.PermissionMapper;
import com.example.demo.rbac.permission.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        return this.getBaseMapper().getPermissionsByUserId(userId);
    }
}
