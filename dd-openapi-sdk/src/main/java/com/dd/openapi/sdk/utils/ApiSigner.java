package com.dd.openapi.sdk.utils;

import com.dd.openapi.main.common.api.auth.ApiAuthConstants;
import lombok.Data;

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
public class ApiSigner {

    private final String accessKey;
    private final String secretKey;

    public ApiSigner(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    // 生成签名并返回请求头
    public SortedMap<String, String> generateHeaders(Map<String, String> params) {
        long timestamp = System.currentTimeMillis();

        // 1. 构建待签名字符串
        String signContent = buildSignContent(timestamp, params);

        // 2. 计算HMAC-SHA256签名
        String signature = hmacSha256(secretKey, signContent);

        // 3. 构造请求头
        SortedMap<String, String> headers = new TreeMap<>();
        headers.put(ApiAuthConstants.ACCESS_KEY_HEADER, accessKey);
        headers.put(ApiAuthConstants.TIMESTAMP_HEADER, String.valueOf(timestamp));
        headers.put(ApiAuthConstants.SIGNATURE_HEADER, signature);
        return headers;
    }

    public String buildSignContent(long timestamp, Map<String, String> params) {
        // 1.1 按字典序排序参数
        SortedMap<String, String> sortedParams = new TreeMap<>(params);

        // 1.2 拼接键值对
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp);
        sortedParams.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));

        // 删除末尾多余的&
        if (!sortedParams.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String hmacSha256(String secret, String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("HMAC计算失败", e);
        }
    }
}
