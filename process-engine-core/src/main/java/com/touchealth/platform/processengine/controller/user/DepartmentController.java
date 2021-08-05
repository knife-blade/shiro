package com.touchealth.platform.processengine.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.common.basic.exception.CommonCode;
import com.touchealth.common.basic.utils.AssertUtil;
import com.touchealth.platform.processengine.constant.ValidGroup;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.user.Department;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentDto;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentTreeDto;
import com.touchealth.platform.processengine.pojo.request.user.DepartmentOpRequest;
import com.touchealth.platform.processengine.pojo.request.user.DepartmentRequest;
import com.touchealth.platform.processengine.service.user.DepartmentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工部门表 前端控制器
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/department")
public class DepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询部门和岗位
     * @param req
     * @return
     */
    @GetMapping("/treeList")
    public PageData<DepartmentTreeDto> treeList(DepartmentRequest req,
                                                @RequestHeader String channelNo) {
        Long pageNo = req.getPageNo();
        Long pageSize = req.getPageSize();
        Long departmentId = req.getDepartmentId();
        Long postJobId = req.getPostJobId();
        channelNo = Optional.ofNullable(req.getChannelNo()).orElse(channelNo);
        Integer type = req.getType()==null?0:req.getType();
        return departmentService.pageTreeByDeptIdAndPostJobId(channelNo, type, departmentId, postJobId, pageNo, pageSize);
    }

    /**
     * 查询所有部门
     * @param req
     * @return
     */
    @GetMapping("/list")
    public List<DepartmentDto> list(DepartmentRequest req,
                                    @RequestHeader String channelNo) {
        String departmentName = req.getDepartmentName();

        LambdaQueryWrapper<Department> qw = Wrappers.<Department>lambdaQuery().eq(Department::getChannelNo, channelNo);
        qw.eq(req.getType() != null,Department::getType,req.getType());
        if (StringUtils.isNotEmpty(departmentName)) {
            qw.like(Department::getName, departmentName);
        }

        List<Department> depts = departmentService.list(qw.orderByDesc(Department::getCreatedTime));

        return CollectionUtils.isNotEmpty(depts) ?
                depts.stream().map(o -> new DepartmentDto(o)).collect(Collectors.toList()) :
                new ArrayList<>();
    }

    /**
     * 添加部门
     * @param req
     * @param channelNo
     * @return
     */
    @PostMapping("/add")
    public BooleanDto add(@RequestBody @Valid DepartmentOpRequest req,
                          @RequestHeader String channelNo) {
        AssertUtil.notNull(req.getType(), CommonCode.PARAM_MISSING);
        String name = req.getName();
        req.setChannelNo(channelNo);
        List<Department> depts = departmentService.list(Wrappers.<Department>lambdaQuery()
                .eq(Department::getChannelNo, channelNo)
                .eq(Department::getName, name)
                .eq(req.getType() != null,Department::getType,req.getType())
        );
        if (CollectionUtils.isNotEmpty(depts)) {
            return new BooleanDto(false, "部门已存在");
        }
        return new BooleanDto(departmentService.saveAndCache(req) != null, "很抱歉，添加部门失败，请稍后重试。");
    }

    /**
     * 编辑部门
     * @param req
     * @return
     */
    @PutMapping("/edit")
    public BooleanDto edit(@RequestBody @Validated(ValidGroup.Edit.class) DepartmentOpRequest req,
                           @RequestHeader String channelNo) {
        req.setChannelNo(channelNo);
        Department byId = departmentService.getById(req.getId());
        if (byId == null){
            return new BooleanDto(false, "参数异常!");
        }
        //校验重复
        if(!req.getName().equals(byId.getName())){
            String name = req.getName();
            List<Department> depts = departmentService.list(Wrappers.<Department>lambdaQuery()
                    .eq(Department::getChannelNo, channelNo)
                    .eq(Department::getName, name)
                    .eq(Department::getType,byId.getType())
            );
            if (CollectionUtils.isNotEmpty(depts)) {
                return new BooleanDto(false, "部门已存在");
            }
        }
        return new BooleanDto(departmentService.updateAndCache(req) != null, "很抱歉，更新部门失败，请稍后重试。");
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public BooleanDto delete(@PathVariable("id") Long id) {
        return new BooleanDto(departmentService.deleteAndCache(id), "很抱歉，删除部门失败，请稍后重试。");
    }

    /**
     * 查询科助部门列表
     * @param departmentId
     * @return
     */
    @GetMapping("/kezhu")
    public PageData<DepartmentTreeDto> getDepartmentList(@RequestParam Integer type,
                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) Long departmentId){
        PageData<DepartmentTreeDto> res = departmentService.pageByDeptIdAndType(departmentId, type, pageNo, pageSize);
        return res;
    }

}
