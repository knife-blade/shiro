package com.example.demo.business.logout;

import com.example.demo.common.entity.ResultWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "登出")
@RestController
public class LogoutController {

    @ApiOperation("登出")
    @PostMapping("logout")
    public ResultWrapper<Object> logout() {
        SecurityUtils.getSubject().logout();
        return ResultWrapper.success();
    }
}