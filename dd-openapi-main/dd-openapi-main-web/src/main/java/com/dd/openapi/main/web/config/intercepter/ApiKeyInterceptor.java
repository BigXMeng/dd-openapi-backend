package com.dd.openapi.main.web.config.intercepter;

import com.dd.openapi.main.web.config.exception.DomainException;
import com.dd.openapi.sdk.client.OpenApiClient;
import com.dd.openapi.sdk.utils.ApiSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 11:22
 * @Description 类功能作用说明
 */
@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final OpenApiClient openApiClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 accessKey 和 secretKey
        String accessKey = request.getHeader("accessKey");
        String secretKey = request.getHeader("secretKey");

        // 验证 accessKey 和 secretKey 是否存在
        if (accessKey == null || secretKey == null || accessKey.isEmpty() || secretKey.isEmpty()) {
            throw new DomainException(401, "该接口的请求需要携带accessKey&secretKey请求头");
        }

        // 动态修改SDK元数据 使用当前用户的访问密钥
        openApiClient.setAccessKey(accessKey);
        openApiClient.setAccessKey(secretKey);
        openApiClient.setApiSigner(new ApiSigner(accessKey, secretKey));

        return true;
    }
}