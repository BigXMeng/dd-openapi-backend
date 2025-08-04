package com.dd.openapi.apiserver.common.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/6/27 14:04
 * @Description 自定义错误处理
 */
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        // 根据状态码返回自定义异常
        if (response.status() == 404) {
            return new RuntimeException("服务未找到");
        }
        return new FeignException.BadRequest("请求错误", response.request(), null, null);
    }
}