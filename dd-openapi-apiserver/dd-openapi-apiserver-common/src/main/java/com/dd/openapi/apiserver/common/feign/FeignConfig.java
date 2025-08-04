package com.dd.openapi.apiserver.common.feign;

import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/6/27 14:03
 * @Description 类功能作用说明
 */
@Configuration
public class FeignConfig {

    // 全局超时配置
    @Bean
    public Request.Options options() {
        return new Request.Options(5000, 10000);
    }

    // 错误解码器
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
