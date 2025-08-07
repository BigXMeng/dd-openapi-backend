package com.dd.openapi.sdk.usedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/7 13:34
 * @Description OpenAPI
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dd.openapi.sdk.usedemo"})
public class SDKUseDemo {
    public static void main(String[] args) {
        SpringApplication.run(SDKUseDemo.class, args);
    }
}
