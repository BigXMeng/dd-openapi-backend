package com.dd.openapi.api.server.service;

import com.dd.ms.auth.api.AuthServiceOutside;
import com.dd.ms.auth.vo.UserInfoVO;
import com.dd.openapi.api.server.config.exception.ApiAuthException;
import com.dd.openapi.sdk.model.VerifySignatureParams;
import com.dd.openapi.sdk.utils.ApiSigner;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 11:10
 * @Description 权限验证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    @DubboReference(interfaceClass = AuthServiceOutside.class, version = "1.0")
    private AuthServiceOutside authServiceOutside;

    public void verifySignature(VerifySignatureParams params) throws ApiAuthException {
        // 1. 查询密钥
        UserInfoVO userInfo = authServiceOutside.getUserInfoByToken(params.getUserToken());
        String secretKey = userInfo.getAccount(); // 从用户信息中获取secretKey
        if (secretKey == null) {
            throw new ApiAuthException(403, "无效AccessKey");
        }

        // 2. 重建签名字符串
        ApiSigner signer = new ApiSigner(params.getAccessKey(), secretKey);
        String serverSignContent = signer.buildSignContent(params.getTimestamp(), params.getReqParameters());
        String serverSignature = signer.hmacSha256(secretKey, serverSignContent);

        // 3. 安全比较签名
        if (!MessageDigest.isEqual(
                serverSignature.getBytes(StandardCharsets.UTF_8),
                params.getClientSignature().getBytes(StandardCharsets.UTF_8))) {
            throw new ApiAuthException(403, "签名验证失败");
        }
    }
}
