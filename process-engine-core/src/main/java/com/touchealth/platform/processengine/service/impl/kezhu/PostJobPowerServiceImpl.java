package com.touchealth.platform.processengine.service.impl.kezhu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.common.basic.exception.CommonCode;
import com.touchealth.common.basic.utils.AssertUtil;
import com.touchealth.common.basic.utils.BaseHelper;
import com.touchealth.physical.api.request.branch.BranchPowerUpdateRequest;
import com.touchealth.physical.api.service.branch.BranchPowerApi;
import com.touchealth.platform.processengine.common.constant.CommonObject;
import com.touchealth.platform.processengine.dao.kezhu.PostJobPowerDao;
import com.touchealth.platform.processengine.entity.kezhu.PostJobPower;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.dto.kezhu.BranchPowerDto;
import com.touchealth.platform.processengine.pojo.dto.kezhu.KeZhuBranchDto;
import com.touchealth.platform.processengine.pojo.request.kezhu.KeZhuBranchRequest;
import com.touchealth.platform.processengine.pojo.request.kezhu.KeZhuPowerRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.kezhu.PostJobPowerService;
import com.touchealth.platform.processengine.service.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description : 岗位权限关系
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 14:23
 */
@Service
public class PostJobPowerServiceImpl extends BaseServiceImpl<PostJobPowerDao, PostJobPower> implements PostJobPowerService {

    @Resource
    private PostJobPowerDao postJobPowerDao;

    @Resource
    private UserService userService;

    @Resource
    private BranchPowerApi branchPowerApi;


    /**
     * 保存科助权限
     *
     * @param query
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePower(KeZhuPowerRequest query) {
        List<KeZhuBranchRequest> branchList = query.getBranchList();
        // 校验医院是否重复
        AssertUtil.notEmpty(branchList,CommonCode.PARAM_ERROR);
        List<Long> collect = branchList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        Assert.isTrue(collect.size() == branchList.size(),"医院重复!");
        List<PostJobPower> addList = new ArrayList<>();
        for (KeZhuBranchRequest branchQuery : branchList) {
            List<CommonObject> list = branchQuery.getBranchList();
            // 校验网点是否重复
            Assert.notEmpty(list,"网点不能为空!");
            List<Long> longs = list.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
            Assert.isTrue(longs.size() == list.size(),"网点重复!");
            for (CommonObject objQuery : list) {
                PostJobPower power = new PostJobPower();
                BeanUtils.copyProperties(query,power);
                power.setHospitalId(branchQuery.getId());
                power.setHospitalName(branchQuery.getName());
                power.setBranchId(objQuery.getId());
                power.setBranchName(objQuery.getName());
                addList.add(power);
            }
        }
        // 删除旧的数据
        LambdaQueryWrapper<PostJobPower> deleteQuery = Wrappers.lambdaQuery(PostJobPower.class)
                .eq(PostJobPower::getPostJobId,query.getPostJobId());
        postJobPowerDao.delete(deleteQuery);
        //保存新的
        if(CollectionUtils.isNotEmpty(addList)){
            super.saveBatch(addList);
        }
    }

    /**
     * 根据岗位查询网点列表
     *
     * @param postJobId
     * @return
     */
    @Override
    public List<KeZhuBranchDto> findAllByPostJobId(Long postJobId) {
        QueryWrapper<PostJobPower> queryWrapper = new QueryWrapper<>(new PostJobPower());
        queryWrapper.eq("post_job_id",postJobId);
        List<PostJobPower> postJobPowers = postJobPowerDao.selectList(queryWrapper);
        List<KeZhuBranchDto> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(postJobPowers)){
            Map<Long, List<PostJobPower>> collect = postJobPowers.stream().collect(Collectors.groupingBy(e -> e.getHospitalId()));
            collect.forEach((k,v)->{
                KeZhuBranchDto dto = new KeZhuBranchDto();
                dto.setId(v.get(0).getHospitalId());
                dto.setName(v.get(0).getHospitalName());
                List<CommonObject> objectList = v.stream().map(e -> {
                    CommonObject object = new CommonObject();
                    object.setId(e.getBranchId());
                    object.setName(e.getBranchName());
                    return object;
                }).collect(Collectors.toList());
                dto.setBranchList(objectList);
                result.add(dto);
            });
        }
        return result;
    }

    /**
     * 更新岗位权限
     *
     * @param query
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByJob(KeZhuPowerRequest query) {
        // 岗位关联的网点权限修改之后,该岗位下的所有员工的关联的网点权限同步修改
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(new User());
        queryWrapper.eq("post_job_id",query.getPostJobId());
        List<User> userList = userService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(userList)){
            List<Long> ids = userList.stream().filter(e -> null != e.getPlatformUserId())
                    .map(e -> e.getPlatformUserId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)){
                BranchPowerUpdateRequest updateQuery = new BranchPowerUpdateRequest();
                updateQuery.setIds(ids);
                updateQuery.setHospitalId(query.getHospitalId());
                updateQuery.setBranchId(query.getBranchId());
                branchPowerApi.updateByUserIds(updateQuery);
            }
        }
        // 修改条件
        UpdateWrapper<PostJobPower> updateWrapper = new UpdateWrapper<>(new PostJobPower());
        updateWrapper.eq("post_job_id",query.getPostJobId());
        updateWrapper.eq("hospital_id",query.getHospitalId());
        updateWrapper.eq("branch_id",query.getBranchId());
        // 修改值
        PostJobPower power = new PostJobPower();
        BeanUtils.copyProperties(query,power);
        postJobPowerDao.update(power,updateWrapper);
    }

    /**
     * 查询用户网点权限
     *
     * @param postJobId
     * @param hospitalId
     * @param branchId
     * @return
     */
    @Override
    public BranchPowerDto findByPostJobIdAndHospitalIdAndBranchId(Long postJobId, Long hospitalId, Long branchId) {
        QueryWrapper<PostJobPower> queryWrapper = new QueryWrapper<>(new PostJobPower());
        queryWrapper.eq("post_job_id",postJobId);
        queryWrapper.eq("hospital_id",hospitalId);
        queryWrapper.eq("branch_id",branchId);
        PostJobPower power = postJobPowerDao.selectOne(queryWrapper);
        return BaseHelper.r2t(power,BranchPowerDto.class);
    }

    /**
     * 查询岗位下的所有权限
     *
     * @param postJobId
     * @return
     */
    @Override
    public List<BranchPowerDto> findAllPowerByPostJobId(Long postJobId) {
        QueryWrapper<PostJobPower> queryWrapper = new QueryWrapper<>(new PostJobPower());
        queryWrapper.eq("post_job_id",postJobId);
        List<PostJobPower> postJobPowers = postJobPowerDao.selectList(queryWrapper);
        return BaseHelper.r2t(postJobPowers,BranchPowerDto.class);
    }
}
