package com.dd.openapi.apiserver.common.feign.api;

import com.dd.openapi.apiserver.common.feign.FeignConfig;
import com.dd.openapi.apiserver.common.feign.fallback.OpenApiFeignFallbackFactory;
import com.dd.openapi.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/4 14:36
 * @Description 向外部提供的FeignAPI
 */
@FeignClient(
        name = "dd-openapi-apiserver-web",
        configuration = FeignConfig.class,
        fallbackFactory = OpenApiFeignFallbackFactory.class // 降级工厂
)
public interface OpenApiFeignService {
    @GetMapping("/dd-openapi-apiserver-web/api/v1/feign/generate-number")
    ApiResponse<Integer> generateNumber();
}