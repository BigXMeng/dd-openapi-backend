package com.dd.openapi.main.web;

import com.dd.openapi.main.web.service.external.UserInterfaceInfoOutsideServiceImpl;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:34
 * @Description OpenAPI
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.dd.openapi.main.web",
        "com.dd.openapi.sdk.config",
        "com.dd.openapi.common.exception"
})
@EnableFeignClients(basePackages = "com.dd.openapi.apiserver.common.feign.api")
@MapperScan(basePackages = {"com.dd.openapi.main.web.mapper"})
@EnableDubbo(scanBasePackageClasses = UserInterfaceInfoOutsideServiceImpl.class)
public class OpenAPI {
    public static void main(String[] args) {
        SpringApplication.run(OpenAPI.class, args);
    }
}
