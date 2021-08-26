package com.example.demo.rbac.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.rbac.user.entity.User;

public interface UserService extends IService<User> {
    User getUserByUserName(String userName);
}
