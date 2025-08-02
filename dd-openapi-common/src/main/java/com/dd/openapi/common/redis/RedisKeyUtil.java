package com.dd.openapi.common.redis;

import com.dd.openapi.common.annotation.MetaInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 18:24
 * @Description RedisKeyUtil
 */
public class RedisKeyUtil {

    @MetaInfo("Redis键组成分隔符")
    public static final String SEPARATOR = ":";

    @MetaInfo("用户权限验证合法标识")
    public static final String GATEWAY_AUTH_MARK = "GATEWAY_AUTH_MARK";

    /**
     * 生成用户权限验证的 Redis 键（对 authorizationValue 压缩）
     *
     * @param authorizationValue 原始授权值（如 JWT 或 Token）
     * @return 压缩后的 Redis 键
     * @throws IllegalArgumentException 如果压缩失败
     */
    public static String getUserAuthRedisKey(String authorizationValue) {
        if (authorizationValue == null || authorizationValue.isEmpty()) {
            throw new IllegalArgumentException("Authorization值不可为空");
        }

        // 对长字符串进行哈希压缩（SHA-256 → Base64 编码）
        String compressedValue;
        try {
            compressedValue = compressString(authorizationValue);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("压缩authorization值失败", e);
        }

        return GATEWAY_AUTH_MARK + SEPARATOR + compressedValue;
    }

    /**
     * 压缩字符串（SHA-256 + Base64 编码）
     */
    private static String compressString(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        // 取前 16 字节 + Base64 编码（平衡长度与唯一性）
        byte[] truncatedHash = new byte[16];
        System.arraycopy(hashBytes, 0, truncatedHash, 0, 16);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(truncatedHash);
    }
}
