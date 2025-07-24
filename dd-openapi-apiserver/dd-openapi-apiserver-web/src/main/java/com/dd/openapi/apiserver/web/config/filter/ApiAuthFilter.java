package com.dd.openapi.apiserver.web.config.filter;

import com.dd.openapi.apiserver.web.config.exception.ApiException;
import com.dd.openapi.apiserver.web.service.AuthService;
import com.dd.openapi.common.api.auth.ApiAuthConstants;
import com.dd.openapi.sdk.exception.ApiClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 13:04
 * @Description 签名认证过滤器
 */
@Component
@RequiredArgsConstructor
public class ApiAuthFilter extends OncePerRequestFilter {

    @Value("${spring.application.name}")
    private String currentAppName;

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /* 白名单 */
        String path = request.getRequestURI();
        if (path.startsWith("/doc.html")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.contains("api-docs")
                || path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper req = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (req == null) {
            req = new ContentCachingRequestWrapper(request);
        }

        String accessKey = req.getHeader(ApiAuthConstants.ACCESS_KEY_HEADER);
        String ts = req.getHeader(ApiAuthConstants.TIMESTAMP_HEADER);
        String signature = req.getHeader(ApiAuthConstants.SIGNATURE_HEADER);

        if (accessKey == null || ts == null || signature == null) {
            throw new ApiException(401, "缺少认证头");
        }

        long now = System.currentTimeMillis();
        long clientTimestamp = Long.parseLong(ts);
        if (Math.abs(now - clientTimestamp) > 5 * 60 * 1000) {
            throw new ApiException(401, "请求已过期");
        }

        SortedMap<String, String> params = extractParams(request);
        String requestURI = req.getRequestURI();
        String requestPath = requestURI.replace("/" + currentAppName, "");

        authService.verifySignature(
                accessKey,
                req.getMethod(),
                requestPath,
                params,
                signature
        );

        filterChain.doFilter(req, response);
    }

    /**
     * 从HttpServletRequest中提取参数（支持GET/POST，兼容JSON和Form-Data）
     * @param request HttpServletRequest对象（建议使用ContentCachingRequestWrapper）
     * @return 排序后的参数Map
     * @throws IOException 当JSON解析失败时抛出
     */
    private SortedMap<String, String> extractParams(HttpServletRequest request) throws IOException {
        SortedMap<String, String> params = new TreeMap<>();

        // 1. 首先处理Query参数（GET或POST的URL参数）
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String value = request.getParameter(name); // 自动取第一个值（兼容多值参数）
            params.put(name, value != null ? value : "");
        }

        // 2. 如果是POST请求且存在Body，补充处理Body参数
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            String contentType = request.getContentType();
            if (contentType != null) {
                byte[] body = getRequestBody(request);
                if (body.length > 0) {
                    if (contentType.toLowerCase().contains("application/json")) {
                        mergeJsonBodyParams(body, params);
                    } else if (contentType.toLowerCase().contains("application/x-www-form-urlencoded")) {
                        mergeFormUrlEncodedParams(body, params);
                    }
                    // 可扩展其他Content-Type...
                }
            }
        }

        return params;
    }

    /**
     * 安全获取请求体（支持ContentCachingRequestWrapper）
     */
    private byte[] getRequestBody(HttpServletRequest request) throws IOException {
        if (request instanceof ContentCachingRequestWrapper) {
            return ((ContentCachingRequestWrapper) request).getContentAsByteArray();
        }

        // 原生Java实现
        try (InputStream is = request.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    /**
     * 合并JSON Body参数到目标Map
     */
    private void mergeJsonBodyParams(byte[] body, Map<String, String> targetMap) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            flattenJsonNode(rootNode, "", targetMap);
        } catch (JsonProcessingException e) {
            throw new IOException("JSON解析失败", e);
        }
    }

    /**
     * 展平JSON结构（处理嵌套字段）
     */
    private void flattenJsonNode(JsonNode node, String currentPath, Map<String, String> targetMap) {
        if (node.isValueNode()) {
            targetMap.put(currentPath, node.asText(""));
        } else if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String newPath = currentPath.isEmpty() ? entry.getKey() : currentPath + "." + entry.getKey();
                flattenJsonNode(entry.getValue(), newPath, targetMap);
            });
        }
        // 忽略数组等其他类型
    }

    /**
     * 合并Form-Urlencoded参数
     */
    private void mergeFormUrlEncodedParams(byte[] body, Map<String, String> targetMap) {
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        Arrays.stream(bodyStr.split("&"))
                .map(param -> param.split("=", 2))
                .filter(pair -> pair.length == 2)
                .forEach(pair -> {
                    try {
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = URLDecoder.decode(pair[1], "UTF-8");
                        // 不覆盖已存在的Query参数
                        targetMap.putIfAbsent(key, value);
                    } catch (UnsupportedEncodingException ignored) {
                        // UTF-8必然支持
                    }
                });
    }
}