package com.dd.openapi.apiserver.web.controller.test;

import com.dd.openapi.common.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/4 17:50
 * @Description 类功能作用说明
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feign")
public class FeignController {

    @GetMapping("/generate-number")
    @ApiOperation("生成一个测试数字")
    public ApiResponse<Integer> generateTestNumber() {
        return ApiResponse.success(6688);
    }
}
