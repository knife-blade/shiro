package com.example.demo.rbac.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.rbac.user.entity.User;
import com.example.demo.rbac.user.mapper.UserMapper;
import com.example.demo.rbac.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUserByUserName(String userName) {
        return lambdaQuery().eq(User::getUserName, userName).one();
    }
}
