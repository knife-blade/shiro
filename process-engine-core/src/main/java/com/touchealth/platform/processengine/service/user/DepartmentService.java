package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.Department;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentTreeDto;
import com.touchealth.platform.processengine.pojo.request.user.DepartmentOpRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.io.Serializable;

/**
 * <p>
 * 员工部门表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface DepartmentService extends BaseService<Department> {

    /**
     * 通过部门ID和岗位ID分页查询部门树列表
     *
     * @param channelNo
     * @param departmentId
     * @param postJobId
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageData<DepartmentTreeDto> pageTreeByDeptIdAndPostJobId(String channelNo,Integer type ,Long departmentId, Long postJobId, Long pageNo, Long pageSize);

    Department updateAndCache(DepartmentOpRequest req);

    Department saveAndCache(DepartmentOpRequest req);

    Boolean deleteAndCache(Long id);

    /**
     * 从缓存中获取数据，若缓存中不存在，则查询后放入缓存
     * @param id
     * @return
     */
    Department getCacheById(Serializable id);

    /**
     * 根据部门ID和类型查询部门列表
     * @param departmentId
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageData<DepartmentTreeDto> pageByDeptIdAndType(Long departmentId, Integer type, Integer pageNo, Integer pageSize);
}
