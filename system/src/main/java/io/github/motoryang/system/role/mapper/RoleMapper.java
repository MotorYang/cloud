package io.github.motoryang.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.motoryang.system.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") String userId);
}