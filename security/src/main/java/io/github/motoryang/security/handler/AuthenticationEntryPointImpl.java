package io.github.motoryang.security.handler;

import com.alibaba.fastjson2.JSON;
import io.github.motoryang.common.domain.RestResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("认证失败: {}", authException.getMessage());
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(RestResult.error(401, "认证失败，请重新登录")));
    }

}
