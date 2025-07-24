package com.dd.openapi.sdk.utils;

import com.dd.openapi.common.api.auth.ApiAuthConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.SortedMap;

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

    public HttpHeaders generateHeaders(String httpMethod,
                                       String requestPath,
                                       SortedMap<String, String> params,
                                       String requestBody) {

        long timestamp = System.currentTimeMillis();
        String signContent =  buildSignContent(httpMethod, requestPath, params, requestBody);;
        String signature = hmacSha256(secretKey, signContent);

        HttpHeaders headers = new HttpHeaders();
        headers.add(ApiAuthConstants.ACCESS_KEY_HEADER, accessKey);
        headers.add(ApiAuthConstants.TIMESTAMP_HEADER, String.valueOf(timestamp));
        headers.add(ApiAuthConstants.SIGNATURE_HEADER, signature);
        if (httpMethod.equals(HttpMethod.POST.name())) {
            headers.add(ApiAuthConstants.REQUEST_BODY_HEADER, requestBody);
        } else {
            headers.add(ApiAuthConstants.REQUEST_BODY_HEADER, params.toString());
        }

        return headers;
    }

    public String buildSignContent(String httpMethod, String requestPath, SortedMap<String, String> params, String requestBody) {
        StringBuilder sb = new StringBuilder()
                .append(httpMethod).append('\n')
                .append(requestPath).append('\n');

        if(httpMethod.equals(HttpMethod.GET.name())) {
            if(params == null || params.isEmpty()) {
                return sb.toString();
            }
            params.forEach((k, v) -> sb.append(k).append('=').append(v).append('&'));
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else {
            if(requestBody == null || requestBody.isEmpty()) {
                return sb.toString();
            }
            return sb.append(requestBody).toString();
        }
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
