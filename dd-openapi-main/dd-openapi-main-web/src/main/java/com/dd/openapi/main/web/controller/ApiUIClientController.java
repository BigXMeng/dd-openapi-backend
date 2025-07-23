package com.dd.openapi.main.web.controller;

import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.sdk.client.OpenApiClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 14:10
 * @Description 类功能作用说明
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ui-client")
@Api(tags = "UI客户端测试三方API服务")
public class ApiUIClientController {

    private final OpenApiClient openApiClient;

    @GetMapping("/call/gene-str-api")
    @ApiOperation("添加接口信息")
    public ApiResponse<String> callGeneStrApi() {
        return ApiResponse.success(openApiClient.geneAStr());
    }
}
