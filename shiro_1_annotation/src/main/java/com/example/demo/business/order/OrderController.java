package com.example.demo.business.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
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
    public String add() {
        return "add success";
    }

    @RequiresRoles(value = {"admin,orderManager"}, logical = Logical.OR)
    @ApiOperation(value="删除订单")
    @PostMapping("delete")
    public String delete() {
        return "delete success";
    }

    @RequiresPermissions("order:edit")
    @ApiOperation(value="编辑订单")
    @PostMapping("edit")
    public String edit() {
        return "edit success";
    }

    @RequiresPermissions("order:view")
    @ApiOperation(value="查看订单")
    @GetMapping("view")
    public String view() {
        return "view success";
    }
}