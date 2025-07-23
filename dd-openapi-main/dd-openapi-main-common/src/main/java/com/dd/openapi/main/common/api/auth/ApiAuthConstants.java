package com.dd.openapi.main.common.api.auth;

import com.dd.openapi.main.common.annotation.MetaInfo;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 11:02
 * @Description 类功能作用说明
 */
public class ApiAuthConstants {
    @MetaInfo(value = "访问密钥请求头")
    public static final String ACCESS_KEY_HEADER = "X-Ca-Key";
    @MetaInfo(value = "时间戳请求头")
    public static final String TIMESTAMP_HEADER = "X-Ca-Timestamp";
    @MetaInfo(value = "签名请求头")
    public static final String SIGNATURE_HEADER = "X-Ca-Signature";
    @MetaInfo(value = " ？有效期5分钟")
    public static final long MAX_TIME_DIFF = 5 * 60 * 1000;
}
