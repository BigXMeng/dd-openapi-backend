package com.dd.openapi.sdk.config;

import com.dd.openapi.common.annotation.MetaInfo;
import com.dd.openapi.sdk.client.OpenApiClient;
import com.dd.openapi.sdk.utils.ApiSigner;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 10:04
 * @Description 属性的配置信息从application.yml中来
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties("openapi.client")
public class OpenApiConfig {

    @MetaInfo(
            value = "网关地址（ENDPOINT）",
            example = "http://localhost:18012/dd-openapi-apiserver-web"
    )
    private final String apiBaseUrl = "http://113.45.24.121:18012/dd-openapi-apiserver-web";

    @MetaInfo(
            value = "【访问密钥】公开标识用户或应用(类似用户名) 用于标识请求来源",
            example = "AKIAIOSFODNN7EXAMPLE"
    )
    private String accessKey;

    @MetaInfo(
            value = "【秘密密钥】类似密码 用于签名请求 确保请求未被篡改 必须严格保密！",
            example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\n"
    )
    private String secretKey;

    @Bean
    public OpenApiClient openApiClient() {
        return OpenApiClient.builder()
                .apiBaseUrl(apiBaseUrl)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .apiSigner(new ApiSigner(accessKey, secretKey))
                .restTemplate(restTemplate())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(10000))
                .setReadTimeout(Duration.ofMillis(10000))
                .build();
    }
}
