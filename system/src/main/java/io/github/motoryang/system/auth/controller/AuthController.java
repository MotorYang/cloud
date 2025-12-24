package io.github.motoryang.system.auth.controller;

import io.github.motoryang.common.domain.RestResult;
import io.github.motoryang.system.auth.service.AuthService;
import io.github.motoryang.system.auth.vo.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public RestResult<CaptchaVO> getCaptcha() {
        CaptchaVO captcha = authService.getCaptcha();
        return RestResult.success(captcha);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public RestResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse vo = authService.login(request);
        return RestResult.success("登录成功", vo);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public RestResult<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return RestResult.success("注册成功，请登录");
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public RestResult<?> logout() {
        authService.logout();
        return RestResult.success("退出成功");
    }

}