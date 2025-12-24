package io.github.motoryang.security.config;

import io.github.motoryang.security.interceptor.GatewayProductInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private GatewayProductInterceptor gatewayProductInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gatewayProductInterceptor)
                .addPathPatterns("/**");
    }
}
