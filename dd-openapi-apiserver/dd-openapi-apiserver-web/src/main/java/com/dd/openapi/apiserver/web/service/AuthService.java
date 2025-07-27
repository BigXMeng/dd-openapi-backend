package com.dd.openapi.apiserver.web.service;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.openapi.apiserver.web.config.exception.ApiException;
import com.dd.openapi.sdk.utils.ApiSigner;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.SortedMap;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 11:10
 * @Description 权限验证服务
 */
@Service
public class AuthService {

    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    public void verifySignature(String accessToken,
                                String httpMethod,
                                String requestPath,
                                SortedMap<String, String> params,
                                String requestBody,
                                String clientSignature) {

        String apiKey = userInfoService.getUserApiKeyByToken(accessToken);
        if (apiKey == null) {
            throw new ApiException(403, "无效ApiKey");
        }

        ApiSigner signer = new ApiSigner(apiKey.split("#")[0], apiKey.split("#")[1]);
        String signContent = signer.buildSignContent(httpMethod, requestPath, params, requestBody);
        String signature = signer.hmacSha256(apiKey.split("#")[1], signContent);

        if (!MessageDigest.isEqual(signature.getBytes(), clientSignature.getBytes())) {
            throw new ApiException(403, "签名验证失败");
        }
    }
}

