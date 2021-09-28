package com.example.demo.business.product;

import com.example.demo.common.entity.Result;
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
    @ApiOperation(value="增加产品")
    @PostMapping("add")
    public Result add() {
        return new Result<>().message("product:add success");
    }

    @ApiOperation(value="删除产品")
    @PostMapping("delete")
    public Result delete() {
        return new Result<>().message("product:delete success");
    }

    @ApiOperation(value="编辑产品")
    @PostMapping("edit")
    public Result edit() {
        return new Result<>().message("product:edit success");
    }

    @ApiOperation(value="查看产品")
    @GetMapping("view")
    public Result view() {
        return new Result<>().message("product:view success");
    }
}
