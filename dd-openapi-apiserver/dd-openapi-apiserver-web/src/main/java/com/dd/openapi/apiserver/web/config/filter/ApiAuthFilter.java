package com.dd.openapi.apiserver.web.config.filter;

import com.dd.openapi.apiserver.web.service.AuthService;
import com.dd.openapi.common.api.auth.ApiAuthConstants;
import com.dd.openapi.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 白名单
        String path = request.getRequestURI();
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 验证认证头
        String accessToken = request.getHeader("Authorization");
        String ts = request.getHeader(ApiAuthConstants.TIMESTAMP_HEADER);
        String signature = request.getHeader(ApiAuthConstants.SIGNATURE_HEADER);
        String requestBody = request.getHeader(ApiAuthConstants.REQUEST_BODY_HEADER);
        validateAuthHeaders(accessToken, ts, signature);

        // 验证时间戳
        validateTimestamp(ts);

        // 提取参数
        SortedMap<String, String> params = extractGetParams(request);
        String requestPath = request.getRequestURI().replace("/" + currentAppName, "");

        // 验证签名
        authService.verifySignature(accessToken, request.getMethod(), requestPath, params, requestBody, signature);

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path) {
        return path.startsWith("/doc.html") || path.startsWith("/swagger-ui") || path.startsWith("/swagger-resources")
                || path.contains("api-docs") || path.startsWith("/webjars");
    }

    private void validateAuthHeaders(String accessKey, String ts, String signature) {
        if (accessKey == null || ts == null || signature == null) {
            throw new DomainException(401, "缺少认证头");
        }
    }

    private void validateTimestamp(String ts) {
        long now = System.currentTimeMillis();
        long clientTimestamp = Long.parseLong(ts);
        if (Math.abs(now - clientTimestamp) > 5 * 60 * 1000) {
            throw new DomainException(401, "请求已过期");
        }
    }

    private SortedMap<String, String> extractGetParams(HttpServletRequest request) {
        SortedMap<String, String> params = new TreeMap<>();

        // 处理Query参数
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }

        return params;
    }
}