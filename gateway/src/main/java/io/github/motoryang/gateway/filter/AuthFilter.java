package io.github.motoryang.gateway.filter;

import com.alibaba.fastjson2.JSON;
import io.github.motoryang.common.constant.Constants;
import io.github.motoryang.common.domain.RestResult;
import io.github.motoryang.common.utils.JwtUtils;
import io.github.motoryang.gateway.properties.WhitelistProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private WhitelistProperties whitelistProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取 token
        String token = getToken(request);

        // token 为空
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange.getResponse(), "未登录，请先登录");
        }

        // 验证 token
        if (!jwtUtils.validateToken(token)) {
            return unauthorized(exchange.getResponse(), "Token 无效或已过期，请重新登录");
        }

        try {
            // 从 token 中获取用户信息，传递给下游服务
            String username = jwtUtils.getUsernameFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);

            // 将用户信息添加到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Username", username)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("Token 解析失败", e);
            return unauthorized(exchange.getResponse(), "Token 解析失败");
        }
    }

    /**
     * 判断是否是白名单路径
     */
    private boolean isWhitePath(String path) {
        return whitelistProperties.getWhitelist().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求中获取 token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(Constants.HEADER_TOKEN);
        if (StringUtils.hasText(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            return token.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        RestResult<?> result = RestResult.error(401, message);
        DataBuffer buffer = response.bufferFactory()
                .wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;  // 优先级高，在其他过滤器之前执行
    }
}