package com.dd.openapi.sdk.config;

import com.dd.openapi.common.annotation.MetaInfo;
import com.dd.openapi.sdk.client.OpenApiClient;
import com.dd.openapi.sdk.utils.ApiSigner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@ComponentScan
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("dd.openapi.client") // application.yml的配置以"dd.openapi.client"作为前缀
public class OpenApiConfig {

    @MetaInfo(
            value = "网关地址（ENDPOINT）",
            example = "http://192.168.1.12/10088"
    )
    private String gatewayBaseUrl;

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
                .gatewayBaseUrl(gatewayBaseUrl)
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
