package com.dd.openapi.sdk.client;

import com.dd.openapi.main.common.annotation.MetaInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 10:08
 * @Description OpenApiClient客户端 三方API供应商提供 用户（程序员）使用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiClient {

    @MetaInfo(
            value = "网关地址（ENDPOINT）目前写死",
            example = "http://localhost:18012/dd-openapi-api-server"
    )
    private String GATEWAY_BASEURL;

    @MetaInfo(
            value = "【访问密钥】公开标识用户或应用(类似用户名) 用于标识请求来源",
            example = "AKIAIOSFODNN7EXAMPLE"
    )
    private String accessKey;

    @MetaInfo(
            value = "【秘密密钥】类似密码 用于签名请求 确保请求未被篡改 必须严格保密！",
            example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\n"
    )
    private String secretKey;

    // 内部API调用

}
