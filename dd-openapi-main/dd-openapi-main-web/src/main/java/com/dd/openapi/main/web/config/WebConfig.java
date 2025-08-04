package com.dd.openapi.main.web.config;

import com.dd.openapi.main.web.config.intercepter.ApiKeySetInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 11:37
 * @Description WebConfig (1) 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiKeySetInterceptor apiKeySetInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，指定拦截路径
        registry.addInterceptor(apiKeySetInterceptor)
                .addPathPatterns("/ui-client/call-api/**")
                // 排除页面或接口路径
                .excludePathPatterns("/ui-client/feign/**");
    }
}
