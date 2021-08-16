package com.example.demo.business.rbac.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.business.rbac.permission.entity.Permission;
import com.example.demo.business.rbac.permission.mapper.PermissionMapper;
import com.example.demo.business.rbac.permission.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public Set<String> getPermissionsByUserName(String userName) {
        return this.getBaseMapper().getPermissionsByUserName(userName);
    }
}
