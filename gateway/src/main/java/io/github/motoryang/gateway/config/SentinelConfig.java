package io.github.motoryang.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import io.github.motoryang.common.domain.RestResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Sentinel 限流配置
 */
@Slf4j
@Configuration
public class SentinelConfig {

    @PostConstruct
    public void init() {
        GatewayCallbackManager.setBlockHandler(this::handleBlockException);
    }

    /**
     * 处理各种限流异常
     */
    private Mono<ServerResponse> handleBlockException(ServerWebExchange exchange, Throwable ex) {
        String path = exchange.getRequest().getPath().value();
        String message;

        if (ex instanceof FlowException) {
            log.warn("限流异常 - 路径: {}", path);
            message = "访问频率过高，请稍后再试";
        } else if (ex instanceof DegradeException) {
            log.warn("降级异常 - 路径: {}", path);
            message = "服务暂时不可用，请稍后再试";
        } else if (ex instanceof ParamFlowException) {
            log.warn("热点参数限流 - 路径: {}", path);
            message = "访问过于频繁，请稍后再试";
        } else if (ex instanceof SystemBlockException) {
            log.warn("系统保护规则触发 - 路径: {}", path);
            message = "系统繁忙，请稍后再试";
        } else if (ex instanceof AuthorityException) {
            log.warn("授权规则不通过 - 路径: {}", path);
            message = "没有访问权限";
        } else {
            log.warn("未知限流异常 - 路径: {}", path);
            message = "系统繁忙，请稍后再试";
        }

        return ServerResponse
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(RestResult.error(429, message));
    }
}