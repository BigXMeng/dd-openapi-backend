package com.dd.openapi.main.web.util.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/28 10:32
 * @Description 类功能作用说明
 */
public class CodeGenerator {
    public static void main(String[] args) {
        // 1. 数据源配置
        DataSourceConfig dsc = new DataSourceConfig.Builder(
                "jdbc:mysql://localhost:3306/dd_openapi?useSSL=false&serverTimezone=UTC",
                "root",
                "root"
        ).build();

        // 2. 全局配置
        GlobalConfig gc = new GlobalConfig.Builder()
                // 生成到顶层project所在文件夹 E:\A_Docs\DD\A_PROJECTS\O_OpenApi\dd-openapi-back下
                // 最终文件目录 E:\A_Docs\DD\A_PROJECTS\O_OpenApi\dd-openapi-back\src\main\java\com\dd\openapi\main\web
                .outputDir(System.getProperty("user.dir") + "/src/main/java/com/dd/openapi/main/web") // 输出目录
                .author("liuxianmeng") // 作者
                .build();

        // 3. 包名配置
        PackageConfig pc = new PackageConfig.Builder()
                //.parent("gene") // 父包名
                //.moduleName("dd-openapi-main-web") // 模块名（可选）
                .entity("DO") // 实体类包名
                .mapper("mapper") // Mapper包名
                .service("service") // Service包名
                .controller("controller") // Controller包名
                .build();

        // 4. 策略配置
        StrategyConfig strategy = new StrategyConfig.Builder()
                .addInclude("user_interface_info") // 要生成的表名（多个用逗号分隔）
                //.addTablePrefix("t_", "sys_") // 表前缀过滤（生成实体类时去掉前缀）
                .entityBuilder()
                .naming(NamingStrategy.underline_to_camel) // 表名转驼峰
                .columnNaming(NamingStrategy.underline_to_camel) // 列名转驼峰
                .enableLombok() // 使用Lombok
                .controllerBuilder()
                .enableRestStyle() // 生成@RestController
                .build();

        // 5. 执行生成
        AutoGenerator generator = new AutoGenerator(dsc);
        generator.global(gc)
                .packageInfo(pc)
                .strategy(strategy)
                .execute(); // 执行
    }
}
