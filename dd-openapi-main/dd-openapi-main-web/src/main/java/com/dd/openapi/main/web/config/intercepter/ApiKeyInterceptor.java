package com.dd.openapi.main.web.config.intercepter;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.openapi.main.web.config.exception.DomainException;
import com.dd.openapi.sdk.client.OpenApiClient;
import com.dd.openapi.sdk.utils.ApiSigner;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 accessKey 和 secretKey
        String token = request.getHeader("Authorization");
        String apiKeys = userInfoService.getUserApiKeyByToken(token.substring(7));

        // 验证 accessKey 和 secretKey 是否存在
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new DomainException(401, "该接口的请求需要携带Authorization请求头");
        }

        // 动态修改SDK元数据 使用当前用户的访问密钥
        openApiClient.setAccessKey(apiKeys.split("#")[0]);
        openApiClient.setAccessKey(apiKeys.split("#")[1]);
        openApiClient.setApiSigner(new ApiSigner(apiKeys.split("#")[0], apiKeys.split("#")[1]));

        return true;
    }
}