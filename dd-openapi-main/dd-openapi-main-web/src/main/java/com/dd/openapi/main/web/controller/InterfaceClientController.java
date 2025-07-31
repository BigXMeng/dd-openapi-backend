package com.dd.openapi.main.web.controller;

import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.common.exception.DomainException;
import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.main.web.model.req.CallUUIDGeneReq;
import com.dd.openapi.main.web.util.AuthUtils;
import com.dd.openapi.sdk.client.OpenApiClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
public class InterfaceClientController {

    private final OpenApiClient openApiClient;
    private final AuthUtils authUtils;

    // TODO 异常处理

    @GetMapping("/call-api/gene-str-api")
    @ApiOperation("返回一个字符串")
    public ApiResponse<String> callGeneStrApi() {
        String accessToken = authUtils.getAccessToken();
        return openApiClient.geneAStr(accessToken);
    }

    @ApiOperation("获取本地IP信息")
    @GetMapping("/call-api/ip-info")
    public ApiResponse<IpInfoResp> ipInfo() {
        String accessToken = authUtils.getAccessToken();
        return openApiClient.ipInfo(accessToken);
    }

    //@ApiOperation("二维码")
    //@GetMapping("/call-api/qr-code/{content}")
    //public ApiResponse<QrCodeResp> qrCode(@PathVariable("content") String content)
    //        throws UnsupportedEncodingException {
    //    String accessToken = authUtils.getAccessToken();
    //    return openApiClient.qrCode(content, accessToken);
    //}

    @PostMapping("/call-api/uuid-batch")
    @ApiOperation(value = "批量生成UUID", notes = "生成指定数量的UUID（最多1000个）")
    public ApiResponse<String> uuidBatch(@RequestBody CallUUIDGeneReq req) {
        log.info("M uuidBatch() req = {}", req.toString());
        String accessToken = authUtils.getAccessToken();
        return openApiClient.uuidBatch(req.getNum(), accessToken);
    }
}
