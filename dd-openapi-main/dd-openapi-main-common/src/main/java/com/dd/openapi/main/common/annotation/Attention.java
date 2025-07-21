package com.dd.openapi.main.common.annotation;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/52 11:35
 * @Description 注意 说明注解
 */
public @interface Attention {

    /*
     * 强调某个字段需要注意的事项
     */
    String value() default "";
}
