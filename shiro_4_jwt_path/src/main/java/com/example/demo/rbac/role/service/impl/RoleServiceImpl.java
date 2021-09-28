package com.example.demo.rbac.role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.rbac.role.entity.Role;
import com.example.demo.rbac.role.mapper.RoleMapper;
import com.example.demo.rbac.role.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Set<String> getRolesByUserId(String userId) {
        Long id = Long.parseLong(userId);
        return this.getBaseMapper().getRolesByUserId(id);
    }
}
