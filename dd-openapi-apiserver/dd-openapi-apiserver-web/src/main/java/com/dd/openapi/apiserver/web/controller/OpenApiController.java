package com.dd.openapi.apiserver.web.controller;

import com.dd.openapi.apiserver.common.req.GeneUUIDReq;
import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.web.service.OpenApiServiceImpl;
import com.dd.openapi.common.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    private final OpenApiServiceImpl openApiServiceImpl;

    @GetMapping("/gene-a-str")
    @ApiOperation("获取一个'JustDOIt.'字符串")
    public ApiResponse<String> geneAStr() {
        log.info("C OpenApiController M geneAStr() ..");
        return ApiResponse.success(openApiServiceImpl.geneAStr());
    }

    @GetMapping("/ip-info")
    @ApiOperation(
            value = "获取IP地址详细信息",
            notes = "根据请求头X-Forwarded-For获取真实IP，调用第三方接口解析地理位置信息"
    )
    public ApiResponse<IpInfoResp> ipInfo(HttpServletRequest request) {
        return ApiResponse.success(openApiServiceImpl.ipInfo(request));
    }

    @PostMapping("/uuid-batch")
    @ApiOperation(value = "批量生成UUID", notes = "生成指定数量的UUID（最多1000个）")
    public ApiResponse<String> uuidBatch(
            @ApiParam(value = "生成数量参数", required = true, example = "{5}")
            @RequestBody GeneUUIDReq req) {
        log.info("C OpenApiController M uuidBatch() req = {}", req);
        return ApiResponse.success(openApiServiceImpl.uuidBatch(req));
    }

    //@GetMapping("/qr-code")
    //@ApiOperation(
    //        value = "生成二维码图片",
    //        notes = "将文本内容编码为Base64格式的PNG二维码图片，可自定义大小和纠错级别"
    //)
    //public ApiResponse<QrCodeResp> qrCode(
    //        @ApiParam(value = "二维码内容", required = true, example = "https://example.com")
    //        @RequestParam String text,
    //
    //        @ApiParam(value = "宽度（像素）", defaultValue = "300")
    //        @RequestParam(required = false, defaultValue = "300") int width,
    //
    //        @ApiParam(value = "高度（像素）", defaultValue = "300")
    //        @RequestParam(required = false, defaultValue = "300") int height,
    //
    //        @ApiParam(value = "纠错级别", allowableValues = "L,M,Q,H", defaultValue = "M")
    //        @RequestParam(required = false, defaultValue = "M") String errorCorrection) {
    //
    //    // 参数校验
    //    if (StrUtil.isBlank(text)) {
    //        throw new DomainException(400, "二维码内容不能为空");
    //    }
    //    if (width <= 0 || height <= 0) {
    //        throw new DomainException(400, "宽度和高度必须大于0");
    //    }
    //
    //    try {
    //        // 配置二维码参数
    //        QrConfig config = new QrConfig(width, height);
    //        config.setErrorCorrection(convertErrorCorrectionLevel(errorCorrection));
    //
    //        // 生成Base64二维码
    //        String base64 = QrCodeUtil.generateAsBase64(text, config, "png");
    //        return ApiResponse.success(
    //                new QrCodeResp("data:image/png;base64," + base64)
    //        );
    //    } catch (Exception e) {
    //        throw new DomainException(500, "二维码生成失败: " + e.getMessage());
    //    }
    //}
}

