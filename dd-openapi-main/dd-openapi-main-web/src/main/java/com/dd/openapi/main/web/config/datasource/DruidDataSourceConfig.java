package com.dd.openapi.main.web.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/3/7 23:34
 * @Description Druid数据库连接池配置
 * Druid 作为一个数据库连接池，可以提高数据库操作的性能、稳定性和安全性，
 * 是 Java 应用开发中常用的数据库连接池解决方案之一。
 */
@Slf4j
@Configuration
public class DruidDataSourceConfig {
    @Bean
    /**
     * @ConfigurationProperties("spring.datasource")
     * 表示将application.yml或application.properties文件中spring.datasource
     * 下的所有属性值绑定到DruidDataSource对象的相应属性中。如下配置：
     * ```
     * spring:
     *   datasource:
     *     url: jdbc:mysql://localhost:3306/yourdb
     *     username: yourusername
     *     password: yourpassword
     *     driver-class-name: com.mysql.cj.jdbc.Driver
     * ```
     * 中的属性将自动映射到DruidDataSource的setUrl、setUsername、setPassword和setDriverClassName方法中。
     */
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        log.info("当前数据源 = {}", druidDataSource.getClass());
        return druidDataSource;
    }
}
