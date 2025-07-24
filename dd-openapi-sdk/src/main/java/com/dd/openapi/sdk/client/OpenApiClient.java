package com.dd.openapi.sdk.client;

import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.common.resp.JsonDiffReq;
import com.dd.openapi.apiserver.common.resp.JsonDiffResp;
import com.dd.openapi.apiserver.common.resp.QrCodeResp;
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

    /* ---------- 0. 生成一个字符串 ---------- */
    public String geneAStr() {
        return callApi( "/api/open/gene-a-str", HttpMethod.GET, null, String.class);
    }

    /* ---------- 1. IP 信息 ---------- */
    public IpInfoResp ipInfo() {
        return callApi("/api/open/ip-info", HttpMethod.GET, null, IpInfoResp.class);
    }

    /* ---------- 2. 二维码 ---------- */
    public QrCodeResp qrCode(String text) throws UnsupportedEncodingException {
        HashMap<String, Object> body = new HashMap<>();
        body.put("text", text);
        return callApi("/api/open/qr-code", HttpMethod.GET, body, QrCodeResp.class);
    }

    /* ---------- 3. 批量 UUID ---------- */
    public List<String> uuidBatch(int count) {
        HashMap<String, Integer> body = new HashMap<>();
        body.put("count", count);
        return callApi("/api/open/uuid-batch", HttpMethod.POST, body, List.class);
    }

    /* ---------- 4. JSON 差异 ---------- */
    public JsonDiffResp jsonDiff(JsonDiffReq req) {
        return callApi("/api/open/json-diff", HttpMethod.POST, req, JsonDiffResp.class);
    }

    /**
     * 调用API并返回指定类型的响应对象
     * @param path API路径
     * @param method HTTP方法
     * @param requestBody 请求体对象
     * @param responseType 响应数据类型Class对象
     * @param <T> 响应数据类型
     * @return 解析后的响应数据对象
     * @throws ApiClientException API调用异常
     */
    private <T> T callApi(String path,
                          HttpMethod method,
                          Object requestBody,
                          Class<T> responseType) {
        // 1. 构建完整URL和签名参数
        String url = gatewayBaseUrl + path;
        SortedMap<String, String> params = extractParams(requestBody);
        // 如果是GET请求则在后面添加请求参数
        if(method == HttpMethod.GET) {
            url = url + "?" + params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        }
        // 2. 生成签名请求头
        HttpHeaders headers = apiSigner.generateHeaders(method.name(), path, params);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 执行请求
        try {
            ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                    url,
                    method,
                    new HttpEntity<>(method != HttpMethod.GET ? requestBody : null, headers),
                    new ParameterizedTypeReference<ApiResponse<T>>() {}
            );

            // 5. 处理响应
            return handleResponse(response, responseType);
        } catch (RestClientException e) {
            throw new ApiClientException(500, "API调用失败: " + e.getMessage());
        }
    }

    /**
     * URL编码参数值
     */
    private String encodeParam(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("参数编码失败", e);
        }
    }

    /**
     * 处理API响应
     */
    private <T> T handleResponse(ResponseEntity<ApiResponse<T>> response, Class<T> responseType) {
        ApiResponse<T> body = response.getBody();

        // 1. 检查基础响应
        if (body == null) {
            throw new ApiClientException(500, "空响应");
        }
        if (body.getCode() >= 300) {
            throw new ApiClientException(body.getCode(), body.getMessage());
        }

        // 2. 处理泛型数据
        Object data = body.getData();
        if (data == null) {
            return null;
        }

        // 3. 类型安全转换
        if (responseType.isInstance(data)) {
            return responseType.cast(data);
        }

        // 4. 动态转换（处理LinkedHashMap情况）
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(data, responseType);
        } catch (IllegalArgumentException e) {
            throw new ApiClientException(500, "响应数据转换失败: " + e.getMessage());
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
