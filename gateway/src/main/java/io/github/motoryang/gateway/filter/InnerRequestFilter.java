package io.github.motoryang.gateway.filter;

import io.github.motoryang.common.constant.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 添加内部调用标识过滤器
 * 所有通过网关的请求都会添加内部调用标识
 */
@Component
public class InnerRequestFilter implements GlobalFilter, Ordered {

    @Value("${inner.secret:pL8YxZrC3sA6Q0K7M2H4W5T9VJENdFBRUeGmX}")
    private String innerSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 为所有请求添加内部调用标识
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(Constants.HEADER_INNER_FLAG, innerSecret)
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -50;  // 在认证过滤器之后执行
    }
}