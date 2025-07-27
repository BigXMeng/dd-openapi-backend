package com.dd.openapi.apiserver.web.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpUtil;
import com.dd.openapi.apiserver.common.req.GeneUUIDReq;
import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.common.resp.QrCodeResp;
import com.dd.openapi.apiserver.web.config.exception.ApiException;
import com.dd.openapi.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String IP_API_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    @GetMapping("/gene-a-str")
    @ApiOperation("获取一个'JustDOIt.'字符串")
    public ApiResponse<String> geneAStr() {
        log.info("C OpenApiController M geneAStr() ..");
        return ApiResponse.success("JustDOIt." + generateRandomString(10));
    }

    /**
     * IP信息查询
     */
    @GetMapping("/ip-info")
    @ApiOperation(
            value = "获取IP地址详细信息",
            notes = "根据请求头X-Forwarded-For获取真实IP，调用第三方接口解析地理位置信息"
    )
    public ApiResponse<IpInfoResp> ipInfo(HttpServletRequest request) {
        try {
            // 1. 获取客户端真实IP（支持代理场景）
            String ip = resolveRealClientIp(request);

            // 2. 调用第三方API获取IP信息
            String apiUrl = String.format(IP_API_URL, ip);
            String respBody = HttpUtil.get(apiUrl);

            // 3. 提取有效JSON数据（处理回调函数包裹）
            String jsonData = extractJsonData(respBody).trim();

            // 4. 解析JSON并构建响应对象
            IpInfoResp ipInfoResp = objectMapper.readValue(jsonData, IpInfoResp.class);
            return ApiResponse.success(ipInfoResp);
        } catch (IOException e) {
            throw new ApiException(500, "IP信息查询服务异常: " + e.getMessage());
        }
    }

    /**
     * 二维码生成
     */
    @GetMapping("/qr-code")
    @ApiOperation(
            value = "生成二维码图片",
            notes = "将文本内容编码为Base64格式的PNG二维码图片，可自定义大小和纠错级别"
    )
    public ApiResponse<QrCodeResp> qrCode(
            @ApiParam(value = "二维码内容", required = true, example = "https://example.com")
            @RequestParam String text,

            @ApiParam(value = "宽度（像素）", defaultValue = "300")
            @RequestParam(required = false, defaultValue = "300") int width,

            @ApiParam(value = "高度（像素）", defaultValue = "300")
            @RequestParam(required = false, defaultValue = "300") int height,

            @ApiParam(value = "纠错级别", allowableValues = "L,M,Q,H", defaultValue = "M")
            @RequestParam(required = false, defaultValue = "M") String errorCorrection) {

        // 参数校验
        if (StrUtil.isBlank(text)) {
            throw new ApiException(400, "二维码内容不能为空");
        }
        if (width <= 0 || height <= 0) {
            throw new ApiException(400, "宽度和高度必须大于0");
        }

        try {
            // 配置二维码参数
            QrConfig config = new QrConfig(width, height);
            config.setErrorCorrection(convertErrorCorrectionLevel(errorCorrection));

            // 生成Base64二维码
            String base64 = QrCodeUtil.generateAsBase64(text, config, "png");
            return ApiResponse.success(
                    new QrCodeResp("data:image/png;base64," + base64)
            );
        } catch (Exception e) {
            throw new ApiException(500, "二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 转换纠错级别
     */
    private ErrorCorrectionLevel convertErrorCorrectionLevel(String level) {
        switch (level.toUpperCase()) {
            case "L": return ErrorCorrectionLevel.L;
            case "Q": return ErrorCorrectionLevel.Q;
            case "H": return ErrorCorrectionLevel.H;
            default:  return ErrorCorrectionLevel.M; // 默认中等纠错
        }
    }

    /**
     * 批量生成UUID
     */
    @PostMapping("/uuid-batch")
    @ApiOperation(value = "批量生成UUID", notes = "生成指定数量的UUID（最多1000个）")
    public ApiResponse<List<String>> uuidBatch(
            @ApiParam(value = "生成数量参数", required = true, example = "{5}")
            @RequestBody GeneUUIDReq req) {

        List<String> uuids = IntStream.range(0, req.getCount())
                .mapToObj(i -> UUID.randomUUID().toString())
                .collect(Collectors.toList());

        return ApiResponse.success(uuids);
    }

    // ========== 私有工具方法 ==========

    /**
     * 解析客户端真实IP（支持代理/Nginx/CDN等场景）
     * 优先级：X-Real-IP > X-Forwarded-For > Proxy-Client-IP > WL-Proxy-Client-IP > RemoteAddr
     */
    private String resolveRealClientIp(HttpServletRequest request) {
        // 1. 检查常见代理服务器的头部（按优先级排序）
        String[] headersToCheck = {
                "X-Real-IP",
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                // 处理X-Forwarded-For的多IP情况（取第一个有效IP）
                if ("X-Forwarded-For".equals(header)) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 2. 直接获取远程地址（处理本地测试场景）
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "127.0.0.1".equals(remoteAddr)) {
            return getLocalMachineRealIp(); // 本地环境获取本机实际IP
        }
        return remoteAddr;
    }

    /**
     * 校验IP是否有效
     */
    private boolean isValidIp(String ip) {
        return !StrUtil.isBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取本机真实IP（仅在开发环境需要）
     */
    private String getLocalMachineRealIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1"; // 兜底
        }
    }

    /**
     * 提取JSON核心数据（处理回调函数包裹）
     */
    private String extractJsonData(String rawResponse) {
        final String PREFIX = "IPCallBack(";
        final String SUFFIX = ");";

        if (rawResponse.contains(PREFIX) && rawResponse.contains(SUFFIX)) {
            return StrUtil.subBetween(rawResponse, PREFIX, SUFFIX);
        }
        return rawResponse;
    }

    /**
     * 生成一个指定长度的随机字符串。
     *
     * @param length 随机字符串的长度
     * @return 随机生成的字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}

