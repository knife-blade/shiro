package com.example.demo.business.rbac.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.business.rbac.user.entity.User;
import com.example.demo.business.rbac.user.mapper.UserMapper;
import com.example.demo.business.rbac.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
