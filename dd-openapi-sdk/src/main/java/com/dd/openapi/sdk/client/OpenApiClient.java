package com.dd.openapi.sdk.client;

import com.dd.openapi.common.annotation.MetaInfo;
import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.sdk.exception.ApiClientException;
import com.dd.openapi.sdk.utils.ApiSigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/23 10:08
 * @Description OpenApiClient客户端 三方API供应商提供 用户（程序员）使用
 */
@Data
@Builder
public class OpenApiClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MetaInfo(
            value = "网关地址（ENDPOINT）目前写死",
            example = "http://localhost:18012/dd-openapi-api-server"
    )
    private String gatewayBaseUrl;
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
    private ApiSigner apiSigner;
    private RestTemplate restTemplate;

    /***************************************************************************
    ******************************** 内部API调用 ********************************
    ****************************************************************************/

    /* ---------- 生成一个字符串 ---------- */
    public String geneAStr() {
        return callApi( "/api/open/gene-a-str", HttpMethod.GET, null, String.class);
    }

    /* ---------- 通用调用模板 ---------- */
    private <T> T callApi(String path, HttpMethod method,
                          Object requestBody, Class<T> responseType) {
        String url = gatewayBaseUrl + path;
        SortedMap<String, String> params = extractParams(requestBody);

        /* 1. GET 参数拼到 URL，并从签名 map 里移除 */
        if (method == HttpMethod.GET && !params.isEmpty()) {
            String queryString = params.entrySet().stream()
                    .map(e -> {
                        try {
                            return e.getKey() + "=" + URLEncoder.encode(e.getValue(), String.valueOf(StandardCharsets.UTF_8));
                        } catch (UnsupportedEncodingException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            url += "?" + queryString;
            params.clear();   // 关键修复点：不再参与签名
        }

        HttpHeaders headers = apiSigner.generateHeaders(method.name(), path, params);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(method == HttpMethod.GET ? null : requestBody, headers);

        try {
            ResponseEntity<ApiResponse<T>> resp = restTemplate.exchange(
                    url, method, entity,
                    new ParameterizedTypeReference<ApiResponse<T>>() {}
            );
            ApiResponse<T> body = resp.getBody();
            if (body == null || body.getCode() >= 300) {
                throw new ApiClientException(body == null ? 500 : body.getCode(),
                        body == null ? "空响应" : body.getMessage());
            }
            return body.getData();
        } catch (RestClientException e) {
            throw new ApiClientException(500, "API调用失败: " + e.getMessage());
        }
    }

    private SortedMap<String, String> extractParams(Object body) {
        SortedMap<String, String> map = new TreeMap<>();
        if (body == null) return map;

        if (body instanceof Map) {
            ((Map<?, ?>) body).forEach((k, v) -> map.put(String.valueOf(k), v == null ? "" : String.valueOf(v)));
        } else if (!body.getClass().isPrimitive()) {
            objectMapper.convertValue(body, Map.class)
                    .forEach((k, v) -> map.put(String.valueOf(k), v == null ? "" : String.valueOf(v)));
        } else {
            map.put("value", body.toString());
        }
        return map;
    }
}
