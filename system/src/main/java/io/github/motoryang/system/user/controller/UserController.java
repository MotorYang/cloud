package io.github.motoryang.system.user.controller;

import io.github.motoryang.common.domain.RestResult;
import io.github.motoryang.security.utils.SecurityUtils;
import io.github.motoryang.system.user.converter.UserConverter;
import io.github.motoryang.system.user.dto.UserDTO;
import io.github.motoryang.system.user.service.UserService;
import io.github.motoryang.system.user.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserConverter userConverter;

    @GetMapping("/info")
    RestResult<UserVO> info() {
        String username = SecurityUtils.getUsername();
        UserDTO userDTO = userService.info(username);
        return RestResult.success(userConverter.toVO(userDTO));
    }

}
