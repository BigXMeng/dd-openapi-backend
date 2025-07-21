package com.dd.openapi.main.common.annotation;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/1/31 11:35
 * @Description 用于对POJO及其字段进行解释说明
 */
public @interface Explanation {

    /*
     * 此属性用于对所标注的字段进行解释说明
     */
    String value() default "";
}
