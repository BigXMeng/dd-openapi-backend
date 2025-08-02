package com.dd.openapi.common.exception;

import com.dd.openapi.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author liuxianmeng
 * @CreateTime 2024/10/17 23:12
 * @Description 全局异常处理器
 */
@Slf4j
@ComponentScan
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 定义对DomainException类型异常的处理规程
    @ExceptionHandler({DomainException.class})
    public ApiResponse<?> runtimeExceptionHandler(DomainException e) {
        log.error("ApiClientException: " + e.getMessage(), e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
