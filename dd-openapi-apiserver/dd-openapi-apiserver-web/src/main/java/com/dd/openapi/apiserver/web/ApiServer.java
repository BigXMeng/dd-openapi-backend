package com.dd.openapi.apiserver.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 13:23
 * @Description 类功能作用说明
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.dd.openapi.apiserver.web"})
public class ApiServer {
    public static void main(String[] args) {
        SpringApplication.run(ApiServer.class, args);
    }
}
