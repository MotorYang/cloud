package io.github.motoryang.system.user.service;

import io.github.motoryang.system.user.converter.UserConverter;
import io.github.motoryang.system.user.dto.UserDTO;
import io.github.motoryang.system.user.entity.User;
import io.github.motoryang.system.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserConverter userConverter;

    /**
     * 创建用户
     * @param user user信息
     * @param roleIds 角色id集合
     * @return 创建后的用户
     */
    public UserDTO create(User user, List<String> roleIds) {

        String username = user.getUsername();
        User userDB = userMapper.selectUserByUsername(username);
        if (userDB != null) {
            throw new RuntimeException();
        }
        userMapper.insert(user);

        return null;
    }

    /**
     * 获取指定用户信息
     * @param username 用户名
     * @return 用户
     */
    public UserDTO info(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        User user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }
        return userConverter.toDTO(user);
    }

}
