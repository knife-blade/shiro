package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.dao.user.DepartmentDao;
import com.touchealth.platform.processengine.entity.user.Department;
import com.touchealth.platform.processengine.pojo.bo.user.PostJobUserCountBo;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentDto;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentTreeDto;
import com.touchealth.platform.processengine.pojo.dto.user.PostJobDto;
import com.touchealth.platform.processengine.pojo.dto.user.PostJobTreeDto;
import com.touchealth.platform.processengine.pojo.request.user.DepartmentOpRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.DepartmentService;
import com.touchealth.platform.processengine.service.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工部门表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentDao, Department> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public PageData<DepartmentTreeDto> pageTreeByDeptIdAndPostJobId(String channelNo,Integer type , Long departmentId, Long postJobId, Long pageNo, Long pageSize) {
        PageData<DepartmentTreeDto> pageData = new PageData<>(pageNo.intValue(), pageSize.intValue(), 0, new ArrayList<>());
        List<DepartmentTreeDto> treeData = new ArrayList<>();

        // 查询总数量
        Long total = departmentDao.listByDeptIdAndPostJobIdCount(channelNo, type, departmentId, postJobId);
        if (total == null || total <= 0) {
            return pageData;
        }
        pageData.setTotal(total.intValue());

        // 查询记录数
        List<DepartmentDto> departmentDtoList = departmentDao.listByDeptIdAndPostJobId(channelNo, type,departmentId, postJobId,
                (pageNo - 1) * pageSize, pageSize);

        // 查询人数
        if (CollectionUtils.isNotEmpty(departmentDtoList)) {
            List<Long> postJobIdList = departmentDtoList.stream()
                    .flatMap(o -> o.getPostJobs().stream())
                    .map(PostJobDto::getPostJobId).collect(Collectors.toList());
            Map<Long, Long> postJobUserCountMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(postJobIdList)) {
                // 查询所有岗位人数
                List<PostJobUserCountBo> postJobUserCount = userService.countByPostJobIds(postJobIdList);
                if (CollectionUtils.isNotEmpty(postJobUserCount)) {
                    postJobUserCountMap = postJobUserCount.stream().collect(Collectors.toMap(
                            PostJobUserCountBo::getPostJobId,
                            PostJobUserCountBo::getUserCount,
                            (ov, nv) -> Optional.ofNullable(ov).orElse(0L) + Optional.ofNullable(nv).orElse(0L)));
                }
            }

            for (DepartmentDto dd : departmentDtoList) {

                DepartmentTreeDto deptTree = new DepartmentTreeDto();
                Long deptId = dd.getDepartmentId();
                String deptCode = dd.getDepartmentCode();
                String deptName = dd.getDepartmentName();
                int deptUserCnt = 0;
                List<PostJobTreeDto> child = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(postJobIdList)) {
                    List<PostJobDto> postJobs = dd.getPostJobs();
                    if (CollectionUtils.isNotEmpty(postJobs)) {
                        for (PostJobDto pjd : postJobs) {
                            PostJobTreeDto postJobTree = new PostJobTreeDto();
                            Long pjId = pjd.getPostJobId();
                            String pjCode = pjd.getPostJobCode();
                            String pjName = pjd.getPostJobName();
                            Long pjCnt = postJobUserCountMap.getOrDefault(pjId, 0L);
                            deptUserCnt += pjCnt;

                            postJobTree.setId(pjId);
                            postJobTree.setJobCode(pjCode);
                            postJobTree.setJobName(pjName);
                            postJobTree.setCount(pjCnt.intValue());
                            child.add(postJobTree);
                        }
                        deptTree.setCount(deptUserCnt);
                    }
                }
                deptTree.setType(dd.getType());
                deptTree.setId(deptId);
                deptTree.setDeptCod(deptCode);
                deptTree.setDeptName(deptName);
                deptTree.setChildren(child);

                treeData.add(deptTree);
            }
        }
        pageData.setData(treeData);
        return pageData;
    }

    @CachePut(value = RedisConstant.REDIS_CACHE_DEPT, key = "\"_id_\" + #req.id")
    @Override
    public Department updateAndCache(DepartmentOpRequest req) {
        Long id = req.getId();
        String deptName = req.getName();

        Department dept = getById(id);
        if (dept == null) {
            return null;
        }
        boolean updateFlag = update(new Department(),
                Wrappers.<Department>lambdaUpdate()
                        .eq(Department::getId, id)
                        .set(Department::getName, deptName));
        if (updateFlag) {
            dept.setName(deptName);
        }
        return dept;
    }

    @CachePut(value = RedisConstant.REDIS_CACHE_DEPT, key = "\"_id_\" + #result.id")
    @Override
    public Department saveAndCache(DepartmentOpRequest req) {
        String deptName = req.getName();
        String channelNo = req.getChannelNo();
        Department dept = new Department(channelNo, "", deptName);
        dept.setType(req.getType());
        boolean saveFlag = save(dept);
        if (saveFlag) {
            return dept;
        }
        return null;
    }

    @CacheEvict(value = RedisConstant.REDIS_CACHE_DEPT, key = "\"_id_\" + #id")
    @Override
    public Boolean deleteAndCache(Long id) {
        return update(new Department(),
                Wrappers.<Department>lambdaUpdate()
                        .eq(Department::getId, id)
                        .set(Department::getDeletedFlag, CommonConstant.IS_DELETE));
    }

    @Cacheable(value = RedisConstant.REDIS_CACHE_DEPT, key = "\"_id_\" + #id", unless = "#result == null")
    @Override
    public Department getCacheById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据部门ID和类型查询部门列表
     *
     * @param departmentId
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageData<DepartmentTreeDto> pageByDeptIdAndType(Long departmentId, Integer type, Integer pageNo, Integer pageSize) {
        PageData<DepartmentTreeDto> pageData = new PageData<>(pageNo.intValue(), pageSize.intValue(), 0, new ArrayList<>());
        List<DepartmentTreeDto> treeData = new ArrayList<>();

        // 查询总数量
        Long total = departmentDao.countByDeptIdAndType(departmentId,type);
        if (total == null || total <= 0) {
            return pageData;
        }
        pageData.setTotal(total.intValue());

        // 查询记录数
        List<DepartmentDto> departmentDtoList = departmentDao.listByDeptIdAndType(departmentId, type, (pageNo - 1) * pageSize, pageSize);

        // 查询人数
        if (CollectionUtils.isNotEmpty(departmentDtoList)) {
            List<Long> postJobIdList = departmentDtoList.stream()
                    .flatMap(o -> o.getPostJobs().stream())
                    .map(PostJobDto::getPostJobId).collect(Collectors.toList());
            Map<Long, Long> postJobUserCountMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(postJobIdList)) {
                // 查询所有岗位人数
                List<PostJobUserCountBo> postJobUserCount = userService.countByPostJobIds(postJobIdList);
                if (CollectionUtils.isNotEmpty(postJobUserCount)) {
                    postJobUserCountMap = postJobUserCount.stream().collect(Collectors.toMap(
                            PostJobUserCountBo::getPostJobId,
                            PostJobUserCountBo::getUserCount,
                            (ov, nv) -> Optional.ofNullable(ov).orElse(0L) + Optional.ofNullable(nv).orElse(0L)));
                }
            }

            for (DepartmentDto dd : departmentDtoList) {

                DepartmentTreeDto deptTree = new DepartmentTreeDto();
                Long deptId = dd.getDepartmentId();
                String deptCode = dd.getDepartmentCode();
                String deptName = dd.getDepartmentName();
                int deptUserCnt = 0;
                List<PostJobTreeDto> child = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(postJobIdList)) {
                    List<PostJobDto> postJobs = dd.getPostJobs();
                    if (CollectionUtils.isNotEmpty(postJobs)) {
                        for (PostJobDto pjd : postJobs) {
                            PostJobTreeDto postJobTree = new PostJobTreeDto();
                            Long pjId = pjd.getPostJobId();
                            String pjCode = pjd.getPostJobCode();
                            String pjName = pjd.getPostJobName();
                            Long pjCnt = postJobUserCountMap.getOrDefault(pjId, 0L);
                            deptUserCnt += pjCnt;

                            postJobTree.setId(pjId);
                            postJobTree.setJobCode(pjCode);
                            postJobTree.setJobName(pjName);
                            postJobTree.setCount(pjCnt.intValue());
                            child.add(postJobTree);
                        }
                        deptTree.setCount(deptUserCnt);
                    }
                }
                deptTree.setId(deptId);
                deptTree.setDeptCod(deptCode);
                deptTree.setDeptName(deptName);
                deptTree.setChildren(child);

                treeData.add(deptTree);
            }
        }
        pageData.setData(treeData);
        return pageData;
    }

}
