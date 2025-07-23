package com.dd.openapi.main.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 11:21
 * @Description Knife4jConfig 访问链接：http://localhost:18011/dd-openapi-main/doc.html
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    private ApiInfo apiInfo(String title) {
        return new ApiInfoBuilder()
                .title(title)
                .description("API开放平台（dd-openapi）")
                .contact(new Contact("开发者", "https://blog.bigbigmeng.online", "liuxianmeng168@163.com"))
                .version("1.0")
                .build();
    }

    /* 默认/汇总分组 */
    @Bean
    public Docket allApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("API开放平台（dd-openapi）"))
                .groupName("全部接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dd.openapi.main.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /* 接口信息模块分组 */
    @Bean
    public Docket interfaceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("接口信息模块"))
                .groupName("接口信息模块")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dd.openapi.main.web.controller"))
                .build();
    }

    /* 用户模块分组 */
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("用户模块（测试分组 勿使用）"))
                .groupName("用户模块（测试分组 勿使用）")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dd.openapi.main.web.controller"))
                .build();
    }
}