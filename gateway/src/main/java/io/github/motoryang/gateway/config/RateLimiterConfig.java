package io.github.motoryang.gateway.config;

import com.alibaba.nacos.common.utils.StringUtils;
import io.github.motoryang.common.utils.JwtUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 智能限流 Key 解析器
 */
@Slf4j
@Configuration
public class RateLimiterConfig {

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 智能 KeyResolver - 根据不同情况选择不同的限流策略
     */
    @Primary
    @Bean
    public KeyResolver smartKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            // 对于登录接口，使用 IP 限流
            if (path.contains("/auth/login")) {
                String ip = getClientIp(exchange);
                log.debug("登录接口使用 IP 限流: {}", ip);
                return Mono.just("login:" + ip);
            }

            // 对于已认证的请求，使用用户 ID 限流
            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                try {
                    String actualToken = token.substring(7);
                    if (jwtUtils != null && jwtUtils.validateToken(actualToken)) {
                        String userId = jwtUtils.getUserIdFromToken(actualToken);
                        log.debug("认证请求使用用户 ID 限流: {}", userId);
                        return Mono.just("user:" + userId);
                    }
                } catch (Exception e) {
                    log.warn("Token 解析失败，使用 IP 限流", e);
                }
            }

            // 默认使用 IP 限流
            String ip = getClientIp(exchange);
            log.debug("默认使用 IP 限流: {}", ip);
            return Mono.just("ip:" + ip);
        };
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        // 尝试从 X-Forwarded-For 获取
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        // 尝试从 X-Real-IP 获取
        ip = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 从 RemoteAddress 获取
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }
}