package com.dd.openapi.main.web.config;

import com.dd.openapi.main.web.config.intercepter.ApiKeyInterceptor;
import com.dd.openapi.main.web.config.intercepter.AuthInterceptor;
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
    private ApiKeyInterceptor apiKeyInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，指定拦截路径
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/ui-client/call-api/**")
                .excludePathPatterns("/error"); // 排除错误页面
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/ui-client/call-api/**")
                .excludePathPatterns("/error"); // 排除错误页面
    }
}
