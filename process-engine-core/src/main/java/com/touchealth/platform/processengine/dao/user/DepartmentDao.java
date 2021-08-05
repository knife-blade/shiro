package com.touchealth.platform.processengine.dao.user;

import com.touchealth.platform.processengine.entity.user.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工部门表 Mapper 接口
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface DepartmentDao extends BaseMapper<Department> {

    /**
     * 根据部门ID和岗位ID分页查询部门和岗位列表
     *
     * @param channelNo
     * @param departmentId
     * @param postJobId
     * @param startOffset
     * @param size
     * @return
     */
    List<DepartmentDto> listByDeptIdAndPostJobId(@Param("channelNo") String channelNo,
                                                 @Param("type") Integer type,
                                                 @Param("deptId") Long departmentId,
                                                 @Param("postJobId") Long postJobId,
                                                 @Param("start") Long startOffset, @Param("size") Long size);

    /**
     * 根据部门ID和岗位ID分页查询部门和岗位总数
     *
     * @param channelNo
     * @param departmentId
     * @param postJobId
     * @return
     */
    Long listByDeptIdAndPostJobIdCount(@Param("channelNo") String channelNo,
                                       @Param("type") Integer type,
                                       @Param("deptId") Long departmentId,
                                       @Param("postJobId") Long postJobId);

    Long countByDeptIdAndType(@Param("deptId") Long departmentId,@Param("type")Integer type);

    List<DepartmentDto> listByDeptIdAndType( @Param("deptId") Long departmentId,@Param("type")Integer type,
                                             @Param("start") Integer startOffset, @Param("size") Integer size);
}
