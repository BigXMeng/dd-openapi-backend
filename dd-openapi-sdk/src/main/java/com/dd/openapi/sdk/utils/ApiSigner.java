package com.dd.openapi.sdk.utils;

import com.dd.openapi.common.api.auth.ApiAuthConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 11:05
 * @Description Api签名器
 */
@Data
@Builder
@AllArgsConstructor
public class ApiSigner {

    private final String accessKey;
    private final String secretKey;

    public HttpHeaders generateHeaders(String httpMethod, String requestPath,
                                       SortedMap<String, String> params) {
        long timestamp = System.currentTimeMillis();
        String signContent = buildSignContent(httpMethod, requestPath, params);
        String signature = hmacSha256(secretKey, signContent);

        HttpHeaders headers = new HttpHeaders();
        headers.add(ApiAuthConstants.ACCESS_KEY_HEADER, accessKey);
        headers.add(ApiAuthConstants.TIMESTAMP_HEADER, String.valueOf(timestamp));
        headers.add(ApiAuthConstants.SIGNATURE_HEADER, signature);
        return headers;
    }

    public String buildSignContent(String httpMethod, String requestPath, SortedMap<String, String> params) {
        StringBuilder sb = new StringBuilder()
                .append(httpMethod).append('\n')
                .append(requestPath).append('\n');

        if (!params.isEmpty()) {
            params.forEach((k, v) -> sb.append(k).append('=').append(v).append('&'));
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String hmacSha256(String secret, String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HMAC计算失败", e);
        }
    }
}
