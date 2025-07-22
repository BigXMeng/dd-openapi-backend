package com.dd.openapi.main.web.config.exception;

import lombok.Getter;

/**
 * @Author liuxianmeng
 * @CreateTime 2024/10/17 23:15
 * @Description 领域异常
 */
@Getter
public class DomainException extends RuntimeException {
    private final Integer code;
    // 重写父类有参构造器
    public DomainException(int code, String message) {
        super(message);
        this.code = code;
    }
}
