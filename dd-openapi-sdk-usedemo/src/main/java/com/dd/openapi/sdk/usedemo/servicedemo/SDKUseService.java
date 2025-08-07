package com.dd.openapi.sdk.usedemo.servicedemo;

import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.sdk.client.OpenApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/7 13:52
 * @Description 类功能作用说明
 */
@Service
public class SDKUseService {

    // SDK提供的客户端类
    @Autowired
    private OpenApiClient openApiClient;

    /**
     * 测试API调用
     * @return
     */
    public ApiResponse<String> geneAStr() {
        // 覆盖API提供服务地址本地调用测试
        openApiClient.setApiBaseUrl("http://localhost:18012/dd-openapi-apiserver-web");
        ApiResponse<String> stringApiResponse = openApiClient.geneAStr();
        System.out.println(stringApiResponse);
        return stringApiResponse;
    }
}
