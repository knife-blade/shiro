package com.example.demo.business.rbac.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.business.rbac.user.entity.User;

public interface UserService extends IService<User> {
    User getUserByUserName(String userName);
}
