package com.dd.openapi.api.server.config.filter;

import com.dd.openapi.api.server.config.exception.ApiAuthException;
import com.dd.openapi.api.server.service.AuthService;
import com.dd.openapi.common.api.auth.ApiAuthConstants;
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
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
            throw new ApiAuthException(401, "缺少认证头");
        }

        long now = System.currentTimeMillis();
        long clientTimestamp = Long.parseLong(ts);
        if (Math.abs(now - clientTimestamp) > 5 * 60 * 1000) {
            throw new ApiAuthException(401, "请求已过期");
        }

        SortedMap<String, String> params = getSortedParams(req);
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

    private SortedMap<String, String> getSortedParams(ContentCachingRequestWrapper request) throws IOException {
        SortedMap<String, String> map = new TreeMap<>();

        /* QueryString */
        request.getParameterMap().forEach((k, v) -> map.put(k, v[0]));

        /* JSON Body */
        if ("application/json".equalsIgnoreCase(request.getContentType())) {
            byte[] body = request.getContentAsByteArray();
            if (body.length > 0) {
                Map<?, ?> json = objectMapper.readValue(body, Map.class);
                json.forEach((k, v) -> map.put(String.valueOf(k), v == null ? "" : String.valueOf(v)));
            }
        }
        return map;
    }
}