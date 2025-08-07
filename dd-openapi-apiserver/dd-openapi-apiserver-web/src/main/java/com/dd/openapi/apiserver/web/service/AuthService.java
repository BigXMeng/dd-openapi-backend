package com.dd.openapi.apiserver.web.service;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.openapi.common.exception.DomainException;
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
                                String apiAccessKey,
                                String httpMethod,
                                String requestPath,
                                SortedMap<String, String> params,
                                String requestBody,
                                String clientSignature) {

        /**
         * 初始化ApiSigner签名工具类
         *
         * 首先根据token获取API密钥 如果apiAccessKey不为空 则用方法传入的apiAccessKey获取密钥覆盖accessToken获取的密钥
         * 从设计上来说 用户要么从前端UI调式API（根据token获取API密钥） 要么通过在代码引入SDK进行调用（根据方法传入的apiAccessKey获取密钥）
         */
        String apiKey;
        String apiSecretKey = null;
        if (accessToken != null && !accessToken.isEmpty()) {
            apiKey = userInfoService.getUserApiKeyByToken(accessToken);
            if (apiKey == null || apiKey.equals("#")) {
                throw new DomainException(403, "无效ApiKey");
            }
            apiSecretKey = apiKey.split("#")[1];
            if(apiAccessKey != null) {
                apiAccessKey = apiAccessKey.split("#")[0];
            }
        }
        if(apiAccessKey != null) {
            apiSecretKey = userInfoService.getUserInfoByAccessKey(apiAccessKey).getSecretKey();
        }
        ApiSigner signer = new ApiSigner(apiAccessKey, apiSecretKey);

        // 执行API调用签名
        String signContent = signer.buildSignContent(httpMethod, requestPath, params, requestBody);
        String signature = signer.hmacSha256(apiSecretKey, signContent);

        if (!MessageDigest.isEqual(signature.getBytes(), clientSignature.getBytes())) {
            throw new DomainException(403, "签名验证失败");
        }
    }
}

