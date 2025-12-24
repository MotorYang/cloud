package io.github.motoryang.blog.config;

import io.github.motoryang.blog.interceptor.AiRateLimitInterceptor;
import io.github.motoryang.blog.interceptor.ViewCountRateLimitInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BlogWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AiRateLimitInterceptor aiRateLimitInterceptor;

    @Resource
    private ViewCountRateLimitInterceptor viewCountRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // AI 接口限流
        registry.addInterceptor(aiRateLimitInterceptor)
                .addPathPatterns("/ai/chat", "/ai/generate-summary");
        // 文章浏览量防刷过滤器
        registry.addInterceptor(viewCountRateLimitInterceptor)
                .addPathPatterns("/articles/views/*");
    }
}
