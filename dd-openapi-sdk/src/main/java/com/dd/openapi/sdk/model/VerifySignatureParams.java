package com.dd.openapi.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 12:59
 * @Description 类功能作用说明
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySignatureParams {
    String accessKey;
    Long timestamp;
    Map<String, String> reqParameters;
    String clientSignature;
    String userToken;
}
