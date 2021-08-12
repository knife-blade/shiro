package com.example.demo.business.role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.business.role.entity.Role;
import com.example.demo.business.role.mapper.RoleMapper;
import com.example.demo.business.role.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
