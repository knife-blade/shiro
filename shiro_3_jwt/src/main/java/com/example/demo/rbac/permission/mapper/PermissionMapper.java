package com.example.demo.rbac.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.rbac.permission.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionMapper extends BaseMapper<Permission> {
    @Select("SELECT " +
            "    t_permission.`name`  " +
            "FROM " +
            "    t_user, " +
            "    t_user_role_mid, " +
            "    t_role, " +
            "    t_role_permission_mid, " +
            "    t_permission " +
            "WHERE " +
            "    t_user.`id` = #{userId}  " +
            "    AND t_user.id = t_user_role_mid.user_id  " +
            "    AND t_user_role_mid.role_id = t_role.id " +
            "    AND t_role.id = t_role_permission_mid.role_id " +
            "    AND t_role_permission_mid.permission_id = t_permission.id")
    Set<String> getPermissionsByUserId(@Param("userId") Long userId);
}
