package com.example.demo.business.rbac.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.business.rbac.role.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT " +
            "    t_role.`name`  " +
            "FROM " +
            "    t_user, " +
            "    t_user_role_mid, " +
            "    t_role " +
            "WHERE " +
            "    t_user.`user_name` = #{userName}  " +
            "    AND t_user.id = t_user_role_mid.user_id  " +
            "    AND t_user_role_mid.role_id = t_role.id")
    Set<String> getRolesByUserName(@Param("userName")String userName);
}
