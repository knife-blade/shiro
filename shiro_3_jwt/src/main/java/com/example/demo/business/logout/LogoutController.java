package com.example.demo.business.logout;

import com.example.demo.common.entity.Result;
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
    public Result<Object> logout() {
        SecurityUtils.getSubject().logout();
        return new Result();
    }
}
