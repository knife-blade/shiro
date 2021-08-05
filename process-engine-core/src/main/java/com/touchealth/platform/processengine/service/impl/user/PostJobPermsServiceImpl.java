package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.dao.user.PostJobPermsDao;
import com.touchealth.platform.processengine.entity.user.*;
import com.touchealth.platform.processengine.pojo.dto.user.PermTemplateDepartmentDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermTemplatePostJobDto;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.DepartmentService;
import com.touchealth.platform.processengine.service.user.PostJobPermsService;
import com.touchealth.platform.processengine.service.user.UserPermsService;
import com.touchealth.platform.processengine.service.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 岗位权限关系（权限模板）表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Service
public class PostJobPermsServiceImpl extends BaseServiceImpl<PostJobPermsDao, PostJobPerms> implements PostJobPermsService {

    @Autowired
    private PostJobPermsDao postJobPermsDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PostJobPermsService postJobPermsService;
    @Autowired
    private UserPermsService userPermsService;
    @Autowired
    private UserService userService;

    @TransactionalForException
    @Override
    public Boolean saveOrUpdateByPostJobId(PostJob postJob, List<Long> permIds) {
        List<PostJobPerms> postJobPerms = new ArrayList<>();
        Long postJobId = postJob.getId();
        String channelNo = postJob.getChannelNo();

        // 查询岗位下的所有人
        List<User> users = userService.list(Wrappers.<User>lambdaQuery().eq(User::getPostJobId, postJobId));
        if (CollectionUtils.isNotEmpty(users)) {
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            // 删除岗位下所有人的岗位权限
            List<PostJobPerms> oldPostJobPerms = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery().in(PostJobPerms::getPostJobId, postJobId));
            if (CollectionUtils.isNotEmpty(oldPostJobPerms)) {
                List<Long> permsIds = oldPostJobPerms.stream().map(PostJobPerms::getPermsId).collect(Collectors.toList());
                userPermsService.remove(Wrappers.<UserPerms>lambdaQuery().in(UserPerms::getPermsId, permsIds).in(UserPerms::getUserId, userIds));
            }
        }

        // 删除原有岗位下的权限
        remove(Wrappers.<PostJobPerms>lambdaQuery().eq(PostJobPerms::getPostJobId, postJobId));
        if (CollectionUtils.isNotEmpty(permIds)) {
            for (Long permId : permIds) {
                PostJobPerms postJobPerm = new PostJobPerms();
                postJobPerm.setChannelNo(channelNo);
                postJobPerm.setPostJobId(postJobId);
                postJobPerm.setPermsId(permId);
                postJobPerms.add(postJobPerm);
            }
        }
        // 添加岗位下的权限
        boolean saveFlag = saveBatch(postJobPerms);

        // 重置当前用户新的岗位权限
        if (CollectionUtils.isNotEmpty(postJobPerms)) {
            if (CollectionUtils.isNotEmpty(users)) {
                List<UserPerms> userPerms = users.stream()
                        .flatMap(user ->
                                postJobPerms.stream().map(o ->
                                        new UserPerms(user.getChannelNo(), user.getId(), o.getPermsId(), o.getPermsType())))
                        .collect(Collectors.toList());
                boolean userPermsSaveFlag = userPermsService.saveBatch(userPerms);
                if (!userPermsSaveFlag) {
                    log.warn("UserService.updateAndAuth update user perms fail, 问题有点儿大，但是可以手动授权");
                }
            }
        }
        return saveFlag;
    }

    @Override
    public List<PermTemplateDepartmentDto> getDepartmentPermTreeBySearch(String channelNo, String search) {
        List<PermTemplateDepartmentDto> permTemplateDepartmentList = new ArrayList<>();
        Map<Long, Department> deptMap = new HashMap<>();

        List<PermTemplatePostJobDto> permTemplatePostJobList = postJobPermsDao.getAllBySearch(channelNo, search);

        if (CollectionUtils.isNotEmpty(permTemplatePostJobList)) {
            Map<Long, List<PermTemplatePostJobDto>> deptAndPostJobListMap = permTemplatePostJobList.stream().collect(
                    Collectors.toMap(
                            PermTemplatePostJobDto::getDeptId,
                            o -> {
                                List<PermTemplatePostJobDto> arr = new ArrayList<>();
                                arr.add(o);
                                return arr;
                            },
                            (ov, nv) -> {
                                ov.addAll(nv);
                                return ov;
                            }
                    ));
            Set<Long> deptIds = deptAndPostJobListMap.keySet();
            if (CollectionUtils.isNotEmpty(deptIds)) {
                List<Department> departments = departmentService.listByIds(deptIds);
                if (CollectionUtils.isNotEmpty(departments)) {
                    deptMap = departments.stream().collect(Collectors.toMap(Department::getId, o -> o));
                }
            }

            for (Map.Entry<Long, List<PermTemplatePostJobDto>> entry : deptAndPostJobListMap.entrySet()) {
                Long deptId = entry.getKey();
                Department dept = deptMap.get(deptId);
                // 过滤不存在的岗位
                if (dept == null) {
                    continue;
                }

                PermTemplateDepartmentDto permTemplateDepartmentDto = new PermTemplateDepartmentDto();
                permTemplateDepartmentDto.setDeptId(deptId);
                permTemplateDepartmentDto.setDeptName(dept.getName());
                permTemplateDepartmentDto.setPostJobList(entry.getValue());
                permTemplateDepartmentList.add(permTemplateDepartmentDto);
            }
        }

        return permTemplateDepartmentList;
    }

}
