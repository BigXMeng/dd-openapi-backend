package com.dd.openapi.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/2/23 14:43
 * @Description 网关
 */
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.dd.openapi.gateway"})
public class Gateway {
    public static void main(String[] args) {
        SpringApplication.run(Gateway.class, args);
    }
}
