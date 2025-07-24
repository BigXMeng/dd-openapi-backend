package com.dd.openapi.main.web.controller;

import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.common.resp.QrCodeResp;
import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.sdk.client.OpenApiClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
public class ApiClientController {

    private final OpenApiClient openApiClient;

    // TODO 异常处理

    @GetMapping("/call/gene-str-api")
    @ApiOperation("返回一个字符串")
    public ApiResponse<String> callGeneStrApi() {
        return ApiResponse.success(openApiClient.geneAStr());
    }

    @ApiOperation("获取本地IP信息")
    @GetMapping("/call/ip-info")
    public ApiResponse<IpInfoResp> ipInfo() {
        return ApiResponse.success(openApiClient.ipInfo());
    }

    @ApiOperation("二维码")
    @GetMapping("/call/qr-code")
    public ApiResponse<QrCodeResp> qrCode(@RequestParam("text") String text) throws UnsupportedEncodingException {
        return ApiResponse.success(openApiClient.qrCode(text));
    }

    @PostMapping("/call/uuid-batch")
    @ApiOperation(value = "批量生成UUID", notes = "生成指定数量的UUID（最多1000个）")
    public ApiResponse<List<String>> uuidBatch(@RequestParam("count") Integer count) {
        return ApiResponse.success(openApiClient.uuidBatch(count));
    }
}
