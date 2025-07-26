package com.dd.openapi.sdk.client;

import cn.hutool.json.JSONUtil;
import com.dd.openapi.apiserver.common.resp.CallUUIDGeneResp;
import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.common.resp.QrCodeResp;
import com.dd.openapi.common.annotation.MetaInfo;
import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.sdk.exception.ApiClientException;
import com.dd.openapi.sdk.utils.ApiSigner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
    public ApiResponse<String> geneAStr() {
        return callApi("/api/open/gene-a-str", HttpMethod.GET, null, String.class);
    }

    /* ---------- 1. IP 信息 ---------- */
    public ApiResponse<IpInfoResp> ipInfo() {
        return callApi("/api/open/ip-info", HttpMethod.GET, null, IpInfoResp.class);
    }

    /* ---------- 2. 二维码 ---------- */
    public ApiResponse<QrCodeResp> qrCode(String text) throws UnsupportedEncodingException {
        HashMap<String, Object> body = new HashMap<>();
        body.put("text", text);
        return callApi("/api/open/qr-code", HttpMethod.GET, body, QrCodeResp.class);
    }

    /* ---------- 3. 批量 UUID ---------- */
    public ApiResponse<CallUUIDGeneResp> uuidBatch(int count) {
        HashMap<String, Integer> body = new HashMap<>();
        body.put("count", count);
        return callApi("/api/open/uuid-batch", HttpMethod.POST, body, CallUUIDGeneResp.class);
    }

    /**
     * 调用API并返回指定类型的响应对象
     *
     * @param path         API路径
     * @param method       HTTP方法
     * @param requestBody  请求体对象
     * @param responseType 响应数据类型Class对象
     * @param <T>          响应数据类型
     *
     * @return 解析后的响应数据对象
     *
     * @throws ApiClientException API调用异常
     */
    private <T> ApiResponse<T> callApi(String path,
                          HttpMethod method,
                          Object requestBody,
                          Class<T> responseType) {

        // 0. 统一缓存：把任何可能的流式请求体转成字节数组，防止多次读取时关闭
        byte[] cachedBody = toCachedBytes(requestBody);

        // 1. 构建URL和签名参数
        String url = gatewayBaseUrl + path;
        SortedMap<String, String> params = null;    // GET请求用
        HttpHeaders headers = null;                 // 签名请求头
        String requestBodyString = null;            // POST请求体转换成JSON格式

        if (method == HttpMethod.GET) {
            params = extractParams(cachedBody);
            // GET请求追加参数
            url = url + "?" + params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
            headers = apiSigner.generateHeaders(method.name(), path, params, null);

        } else if (method == HttpMethod.POST) {
            if (requestBody != null) {
                requestBodyString = JSONUtil.toJsonStr(requestBody);
            }
            headers = apiSigner.generateHeaders(method.name(), path, null, requestBody.toString());
        }
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 记录请求开始时间
        Instant start = Instant.now();

        // 2. 发送请求
        try {
            ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                    url,
                    method,
                    new HttpEntity<>(method != HttpMethod.GET ? requestBodyString : null, headers),
                    new ParameterizedTypeReference<ApiResponse<T>>() {
                    }
            );

            // 记录请求结束时间
            Instant end = Instant.now();
            // 计算响应时间（单位：毫秒）
            long responseTime = java.time.Duration.between(start, end).toMillis();

            // 3. 处理响应体
            T t = handleResponseBody(response, responseType);
            // 4. 处理响应头
            HttpHeaders respHeaders = response.getHeaders();

            // 5. 返回结果
            ApiResponse<T> success = ApiResponse.success(t);
            success.setResponseHeader(respHeaders.toString());
            success.setResponseTime(responseTime + "");
            return success;
        } catch (RestClientException e) {
            throw new ApiClientException(500, "API调用失败: " + e.getMessage());
        }
    }

    /**
     * 把任何对象安全地转成字节数组：
     * 如果是 byte[]/String 直接返回；
     * 如果是流/Resource 则一次性读出并缓存。
     */
    private byte[] toCachedBytes(Object body) {
        if (body == null) {
            return new byte[0];
        }
        if (body instanceof byte[]) {
            return (byte[]) body;
        }
        if (body instanceof String) {
            return ((String) body).getBytes(StandardCharsets.UTF_8);
        }
        if (body instanceof Resource) {
            try (InputStream is = ((Resource) body).getInputStream()) {
                return StreamUtils.copyToByteArray(is);
            } catch (IOException e) {
                throw new ApiClientException(500, "无法读取请求体" + e.getMessage());
            }
        }
        // 其他 POJO -> JSON
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new ApiClientException(500, "序列化请求体失败" + e.getMessage());
        }
    }

    /**
     * 从缓存的字节数组里提取参数，不再触碰原始流。
     */
    private SortedMap<String, String> extractParams(byte[] cachedBody) {
        if (cachedBody == null || cachedBody.length == 0) {
            return new TreeMap<>();
        }
        try {
            // 这里按你自己的规则解析，例如解析成 Map
            Map<?, ?> map = objectMapper.readValue(cachedBody, Map.class);
            SortedMap<String, String> result = new TreeMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), String.valueOf(v)));
            return result;
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }

    /**
     * 处理API响应体
     */
    private <T> T handleResponseBody(ResponseEntity<ApiResponse<T>> response, Class<T> responseType) {
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
}
