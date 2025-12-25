package io.github.motoryang.system.auth.controller;

import io.github.motoryang.common.domain.RestResult;
import io.github.motoryang.security.domain.RefreshTokenRequest;
import io.github.motoryang.system.auth.service.AuthService;
import io.github.motoryang.system.auth.vo.CaptchaVO;
import io.github.motoryang.system.auth.vo.LoginRequest;
import io.github.motoryang.system.auth.vo.LoginResponse;
import io.github.motoryang.system.auth.vo.RegisterRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * 刷新Token
     */
    @PostMapping("/refresh")
    public RestResult<Map<String, String>> refreshToken(@Validated @RequestBody RefreshTokenRequest request) {
        try {
            Map<String, String> tokens = authService.refreshToken(request.getRefreshToken());
            return RestResult.success(tokens);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return RestResult.error("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public RestResult<?> logout() {
        authService.logout();
        return RestResult.success("退出成功");
    }

}