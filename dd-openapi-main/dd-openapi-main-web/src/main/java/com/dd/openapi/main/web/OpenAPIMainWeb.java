package com.dd.openapi.main.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:34
 * @Description OpenAPIMainWeb
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.dd.openapi.main.web"})
@MapperScan(basePackages = {"com.dd.openapi.main.web.mapper"})
//@EnableDubbo(scanBasePackageClasses = )
public class OpenAPIMainWeb {
    public static void main(String[] args) {
        SpringApplication.run(OpenAPIMainWeb.class, args);
    }
}
