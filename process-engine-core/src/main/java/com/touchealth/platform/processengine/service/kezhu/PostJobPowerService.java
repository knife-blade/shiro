package com.touchealth.platform.processengine.service.kezhu;

import com.touchealth.platform.processengine.entity.kezhu.PostJobPower;
import com.touchealth.platform.processengine.pojo.dto.kezhu.BranchPowerDto;
import com.touchealth.platform.processengine.pojo.dto.kezhu.KeZhuBranchDto;
import com.touchealth.platform.processengine.pojo.request.kezhu.KeZhuPowerRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * @Description : 岗位权限关系
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 14:22
 */
public interface PostJobPowerService extends BaseService<PostJobPower> {

    /**
     * 保存科助权限
     * @param query
     */
    void savePower(KeZhuPowerRequest query);

    /**
     * 根据岗位查询网点列表
     * @param postJobId
     * @return
     */
    List<KeZhuBranchDto> findAllByPostJobId(Long postJobId);

    /**
     * 更新岗位权限
     * @param query
     */
    void updateByJob(KeZhuPowerRequest query);

    /**
     * 查询用户网点权限
     * @param postJobId
     * @param hospitalId
     * @param branchId
     * @return
     */
    BranchPowerDto findByPostJobIdAndHospitalIdAndBranchId(Long postJobId, Long hospitalId, Long branchId);

    /**
     * 查询岗位下的所有权限
     * @param postJobId
     * @return
     */
    List<BranchPowerDto> findAllPowerByPostJobId(Long postJobId);
}
