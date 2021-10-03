package com.example.demo.business.login.controller;

import com.example.demo.business.login.entity.LoginRequest;
import com.example.demo.business.login.entity.LoginVO;
import com.example.demo.rbac.user.entity.User;
import com.example.demo.rbac.user.service.UserService;
import com.example.demo.common.constant.AuthConstant;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.util.auth.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(tags = "登录")
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @ApiOperation("登录")
    @PostMapping("login")
    public LoginVO login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String userName = loginRequest.getUserName();
        String password = loginRequest.getPassword();

        User user = userService.getUserByUserName(userName);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String calculatedPassword = new SimpleHash(AuthConstant.ALGORITHM_TYPE,
                password, user.getSalt(), AuthConstant.HASH_ITERATIONS).toString();

        if (!user.getPassword().equals(calculatedPassword)) {
            throw new BusinessException("用户名或密码不正确");
        }

        String token = JwtUtil.createToken(user.getId().toString());

        response.setHeader(AuthConstant.AUTHENTICATION_HEADER, token);
        // 自定义Header也可以，但浏览器不会存自定义的Header，需要前端自己去存
        // response.setHeader("Authentication", token);

        return fillResult(user);
    }

    private LoginVO fillResult(User user) {
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUserName(user.getUserName());
        return loginVO;
    }
}
