package com.example.demo.business.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "产品")
@RestController
@RequestMapping("product")
public class ProductController {
    @RequiresPermissions("*:product:add")
    @ApiOperation(value="增加产品")
    @PostMapping("add")
    public String add() {
        return "add success";
    }

    @RequiresPermissions("*:product:delete")
    @ApiOperation(value="删除产品")
    @PostMapping("delete")
    public String delete() {
        return "delete success";
    }
    @RequiresPermissions("*:product:edit")
    @ApiOperation(value="编辑产品")
    @PostMapping("edit")
    public String edit() {
        return "edit success";
    }

    @RequiresPermissions("*:product:view")
    @ApiOperation(value="查看产品")
    @GetMapping("view")
    public String view() {
        return "view success";
    }
}
