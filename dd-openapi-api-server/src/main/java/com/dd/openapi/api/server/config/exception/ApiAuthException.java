package com.dd.openapi.api.server.config.exception;

import lombok.Getter;

/**
 * @Author liuxianmeng
 * @CreateTime 2024/10/17 23:15
 * @Description 领域异常
 */
@Getter
public class ApiAuthException extends RuntimeException {
    private final Integer code;
    // 重写父类有参构造器
    public ApiAuthException(int code, String message) {
        super(message);
        this.code = code;
    }
}
