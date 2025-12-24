package io.github.motoryang.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.motoryang.system.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectUserByUsername(@Param("username") String username);

    /**
     * 查询用户的角色权限
     */
    Set<String> selectRoleKeysByUserId(@Param("userId") String userId);

    /**
     * 查询用户的菜单权限
     */
    Set<String> selectPermissionsByUserId(@Param("userId") String userId);

    /**
     * 查询用户列表（带角色信息）
     */
    List<User> selectUserListWithRoles(@Param("user") User user);
}