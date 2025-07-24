package com.dd.openapi.apiserver.web.config.filter;

import com.dd.openapi.apiserver.web.config.exception.ApiException;
import com.dd.openapi.apiserver.web.service.AuthService;
import com.dd.openapi.common.api.auth.ApiAuthConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 白名单
        String path = request.getRequestURI();
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 强制包装成 ContentCachingRequestWrapper，避免后续再读原生流
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            try (InputStream is = req.getInputStream()) {
                StreamUtils.drain(is); // 强制触发读取，把请求体缓存下来
            }
        }

        // 验证认证头
        String accessKey = req.getHeader(ApiAuthConstants.ACCESS_KEY_HEADER);
        String ts = req.getHeader(ApiAuthConstants.TIMESTAMP_HEADER);
        String signature = req.getHeader(ApiAuthConstants.SIGNATURE_HEADER);
        validateAuthHeaders(accessKey, ts, signature);

        // 验证时间戳
        validateTimestamp(ts);

        // 提取参数
        SortedMap<String, String> params = extractParams(req);
        String requestPath = req.getRequestURI().replace("/" + currentAppName, "");

        // 验证签名
        authService.verifySignature(accessKey, req.getMethod(), requestPath, params, signature);

        // 继续过滤链
        filterChain.doFilter(req, response);
    }

    private boolean isWhitelisted(String path) {
        return path.startsWith("/doc.html") || path.startsWith("/swagger-ui") || path.startsWith("/swagger-resources")
                || path.contains("api-docs") || path.startsWith("/webjars");
    }

    private void validateAuthHeaders(String accessKey, String ts, String signature) {
        if (accessKey == null || ts == null || signature == null) {
            throw new ApiException(401, "缺少认证头");
        }
    }

    private void validateTimestamp(String ts) {
        long now = System.currentTimeMillis();
        long clientTimestamp = Long.parseLong(ts);
        if (Math.abs(now - clientTimestamp) > 5 * 60 * 1000) {
            throw new ApiException(401, "请求已过期");
        }
    }

    private SortedMap<String, String> extractParams(HttpServletRequest request) throws IOException {
        SortedMap<String, String> params = new TreeMap<>();

        // 处理Query参数
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }

        // 处理Body参数
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
                }
            }
        }

        return params;
    }

    private byte[] getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return ((ContentCachingRequestWrapper) request).getContentAsByteArray();
        }
        throw new IllegalStateException("Request must be wrapped by ContentCachingRequestWrapper");
    }

    private void mergeJsonBodyParams(byte[] body, Map<String, String> targetMap) throws IOException {
        JsonNode rootNode = objectMapper.readTree(body);
        flattenJsonNode(rootNode, "", targetMap);
    }

    private void flattenJsonNode(JsonNode node, String currentPath, Map<String, String> targetMap) {
        if (node.isValueNode()) {
            targetMap.put(currentPath, node.asText(""));
        } else if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String newPath = currentPath.isEmpty() ? entry.getKey() : currentPath + "." + entry.getKey();
                flattenJsonNode(entry.getValue(), newPath, targetMap);
            });
        }
    }

    private void mergeFormUrlEncodedParams(byte[] body, Map<String, String> targetMap) {
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        Arrays.stream(bodyStr.split("&"))
                .map(param -> param.split("=", 2))
                .filter(pair -> pair.length == 2)
                .forEach(pair -> {
                    try {
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = URLDecoder.decode(pair[1], "UTF-8");
                        targetMap.putIfAbsent(key, value);
                    } catch (UnsupportedEncodingException ignored) {
                    }
                });
    }
}