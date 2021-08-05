package com.touchealth.platform.processengine.controller.user;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.ValidGroup;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.user.PostJob;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.request.user.PostJobOpRequest;
import com.touchealth.platform.processengine.service.user.PostJobService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 员工岗位表 前端控制器
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/postJob")
public class PostJobController extends BaseController {

    @Autowired
    private PostJobService postJobService;

    /**
     * 添加岗位
     * @param req
     * @param channelNo
     * @return
     */
    @PostMapping("/add")
    public BooleanDto add(@RequestBody @Valid PostJobOpRequest req,
                          @RequestHeader String channelNo) {
        req.setChannelNo(channelNo);
        String name = req.getName();
        Long deptId = req.getDeptId();
        List<PostJob> postJobs = postJobService.list(Wrappers.<PostJob>lambdaQuery()
                .eq(PostJob::getDeptId, deptId).eq(PostJob::getName, name));
        Assert.isTrue(CollectionUtils.isEmpty(postJobs), "部门下岗位已存在");
        return new BooleanDto(postJobService.saveAndCache(req) != null, "很抱歉，添加岗位失败，请稍后重试。");
    }

    /**
     * 编辑岗位
     * @param req
     * @return
     */
    @PutMapping("/edit")
    public BooleanDto edit(@RequestBody @Validated(ValidGroup.Edit.class) PostJobOpRequest req) {
        Long id = req.getId();
        String name = req.getName();
        PostJob postJob = postJobService.getCacheById(id);
        Assert.isTrue(postJob != null, "很抱歉，无效的岗位。");
        Long deptId = postJob.getDeptId();
        List<PostJob> postJobs = postJobService.list(Wrappers.<PostJob>lambdaQuery()
                .eq(PostJob::getDeptId, deptId).eq(PostJob::getName, req.getName()));
        Assert.isTrue(CollectionUtils.isEmpty(postJobs), "部门下岗位已存在");
        return new BooleanDto(postJobService.updateAndCache(req) != null, "很抱歉，更新岗位失败，请稍后重试。");
    }

    /**
     * 删除岗位
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public BooleanDto delete(@PathVariable("id") Long id) {
        return new BooleanDto(postJobService.deleteAndCache(id), "很抱歉，删除岗位失败，请稍后重试。");
    }

}
