package com.example.demo.business.order;

import com.example.demo.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单")
@RestController
@RequestMapping("order")
public class OrderController {
    @RequiresPermissions("order:add")
    @ApiOperation(value="增加订单")
    @PostMapping("add")
    public Result add() {
        return new Result<>().message("order:add success");
    }

    @RequiresRoles(value = {"admin", "orderManager"}, logical = Logical.OR)
    @ApiOperation(value="删除订单")
    @PostMapping("delete")
    public Result delete() {
        return new Result<>().message("order:delete success");
    }

    // 管理员或者订单管理员有权限
    @RequiresRoles(value = {"admin", "productManager"}, logical = Logical.OR)
    @ApiOperation(value="编辑订单")
    @PostMapping("edit")
    public Result edit() {
        return new Result<>().message("order:edit success");
    }

    // 此时必须同时满足这两个注解的条件才允许访问
    @RequiresPermissions("order:view")
    @RequiresRoles(value = {"admin", "productManager"}, logical = Logical.OR)
    @ApiOperation(value="查看订单")
    @GetMapping("view")
    public Result view() {
        return new Result<>().message("order:view success");
    }
}
