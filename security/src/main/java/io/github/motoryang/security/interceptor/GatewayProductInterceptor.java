package io.github.motoryang.security.interceptor;

import io.github.motoryang.common.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 网关保护拦截器（外部不能绕过网关调用内部微服务）
 */
@Slf4j
@Component
public class GatewayProductInterceptor implements HandlerInterceptor {

    @Value("${inner.secret:pL8YxZrC3sA6Q0K7M2H4W5T9VJENdFBRUeGmX}")
    private String innerSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取内部标识
        String innerFlag = request.getHeader(Constants.HEADER_INNER_FLAG);
        if (StringUtils.isEmpty(innerFlag) || !innerSecret.equals(innerFlag)) {
            log.warn("非法访问被拦截: {}, innerFlag={}", request.getRequestURI(), innerFlag);
            // 返回 JSON 响应
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":403,\"msg\":\"非法访问\"}");
            writer.flush();
            writer.close();

            return false; // 拦截请求
        }
        // 内部请求，放行
        return true;
    }
}
