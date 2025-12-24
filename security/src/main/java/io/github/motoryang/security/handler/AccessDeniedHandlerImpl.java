package io.github.motoryang.security.handler;

import com.alibaba.fastjson2.JSON;
import io.github.motoryang.common.domain.RestResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限不足处理器
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("权限不足: {}", accessDeniedException.getMessage());
        response.setStatus(403);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(RestResult.error(403, "权限不足，无法访问")));
    }

}
