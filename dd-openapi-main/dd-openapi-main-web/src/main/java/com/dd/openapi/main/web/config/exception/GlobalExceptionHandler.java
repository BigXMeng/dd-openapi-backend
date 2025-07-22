package com.dd.openapi.main.web.config.exception;

import com.dd.openapi.main.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 定义对DomainException类型异常的处理规程
    @ExceptionHandler({DomainException.class})
    public ApiResponse<?> runtimeExceptionHandler(DomainException e) {
        log.error("DomainException: " + e.getMessage(), e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
