package com.dd.openapi.apiserver.web.config.exception;

import com.dd.openapi.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 定义对DomainException类型异常的处理规程
    @ExceptionHandler({ApiException.class})
    public ApiResponse<?> runtimeExceptionHandler(ApiException e) {
        log.error("ApiException: " + e.getMessage(), e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
