package com.dd.openapi.main.common.annotation;

import java.lang.annotation.*;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/1/31 11:35
 * @Description 用于对DubboApi服务进行说明
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ServiceDescription {
    String value() default "DubboApi调用说明";
}
