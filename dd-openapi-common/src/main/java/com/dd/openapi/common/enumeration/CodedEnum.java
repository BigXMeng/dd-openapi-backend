package com.dd.openapi.common.enumeration;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/6/8 11:10
 * @Description 带编码的枚举标准（泛型支持）
 */
public interface CodedEnum<T> extends BusinessEnum {
    T getCode();
}