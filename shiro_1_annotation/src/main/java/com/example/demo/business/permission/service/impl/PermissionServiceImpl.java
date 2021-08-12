package com.example.demo.business.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.business.permission.entity.Permission;
import com.example.demo.business.permission.mapper.PermissionMapper;
import com.example.demo.business.permission.service.PermissionService;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
