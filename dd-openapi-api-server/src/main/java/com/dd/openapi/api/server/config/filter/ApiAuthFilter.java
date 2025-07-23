package com.dd.openapi.api.server.config.filter;

import com.dd.openapi.api.server.config.exception.ApiAuthException;
import com.dd.openapi.api.server.service.AuthService;
import com.dd.openapi.main.common.api.auth.ApiAuthConstants;
import com.dd.openapi.sdk.model.VerifySignatureParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
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

    private final AuthService authService; // 注入密钥服务

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // ===== 白名单：Knife4j 相关资源统统放行 =====
        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/doc.html") ||
                servletPath.startsWith("/swagger-ui") ||
                servletPath.startsWith("/swagger-resources") ||
                servletPath.startsWith("/v2/api-docs") ||
                servletPath.startsWith("/v3/api-docs") ||
                servletPath.startsWith("/webjars")) {
            chain.doFilter(request, response);
            return;
        }

        // 1. 获取请求头
        String accessKey = request.getHeader(ApiAuthConstants.ACCESS_KEY_HEADER);
        String timestamp = request.getHeader(ApiAuthConstants.TIMESTAMP_HEADER);
        String clientSignature = request.getHeader(ApiAuthConstants.SIGNATURE_HEADER);

        // 2. 基本校验
        if (accessKey == null || timestamp == null || clientSignature == null) {
            throw new ApiAuthException(401, "缺少认证头");
        }

        // 3. 验证时间有效性
        Long currentTime = System.currentTimeMillis();
        Long requestTime = Long.parseLong(timestamp);
        if (Math.abs(currentTime - requestTime) > ApiAuthConstants.MAX_TIME_DIFF) {
            throw new ApiAuthException(401, "请求已过期");
        }

        // 4. 获取请求参数
        SortedMap<String, String> params = getSortedParams(request);

        // 5. 验证签名
        authService.verifySignature(
                VerifySignatureParams.builder()
                        .userToken(request.getHeader("Authorization").split(" ")[1])
                        .accessKey(accessKey)
                        .reqParameters(params)
                        .timestamp(requestTime)
                        .clientSignature(clientSignature)
                        .build()
        );

        // 6. 放行请求
        chain.doFilter(request, response);
    }

    private SortedMap<String, String> getSortedParams(HttpServletRequest request) {
        SortedMap<String, String> sortedParams = new TreeMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            sortedParams.put(paramName, request.getParameter(paramName));
        }
        return sortedParams;
    }
}