package com.dd.openapi.apiserver.common.feign.fallback;

import com.dd.openapi.apiserver.common.feign.api.OpenApiFeignService;
import com.dd.openapi.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/6/27 14:07
 * @Description 类功能作用说明
 */
@Slf4j
@Component
public class OpenApiFeignFallbackFactory implements FallbackFactory<OpenApiFeignService> {

    @Override
    public OpenApiFeignService create(Throwable cause) {
        return new OpenApiFeignService() {
            @Override
            public ApiResponse<Integer> generateNumber() {
                return ApiResponse.success(100);
            }
        };
    }
}