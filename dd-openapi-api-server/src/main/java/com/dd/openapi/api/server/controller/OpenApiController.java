package com.dd.openapi.api.server.controller;

import com.dd.openapi.main.common.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 13:15
 * @Description 向外部开放的API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open")
@Api(tags = "向外部开放的API")
public class OpenApiController {
    @PostMapping("/gene-a-str")
    @ApiOperation("获取一个随机的字符串")
    public ApiResponse<String> geneAStr() {
        log.info("C OpenApiController M geneAStr() ..");
        return ApiResponse.success("HOPE");
    }
}

