package com.dd.openapi.apiserver.web.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.dd.openapi.apiserver.common.InterfaceDictionary;
import com.dd.openapi.apiserver.common.req.GeneUUIDReq;
import com.dd.openapi.apiserver.common.resp.IpInfoResp;
import com.dd.openapi.apiserver.web.util.AuthUtils;
import com.dd.openapi.common.annotation.ServiceDescription;
import com.dd.openapi.common.exception.DomainException;
import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.main.web.common.api.UserInterfaceInfoOutsideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/4 14:35
 * @Description 类功能作用说明
 */
@Service
@RequiredArgsConstructor
public class OpenApiServiceImpl {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String IP_API_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final AuthUtils authUtils;

    @DubboReference(interfaceClass = UserInterfaceInfoOutsideService.class, group = "DUBBO_DD_OPENAPI", version = "1.0")
    private UserInterfaceInfoOutsideService userInterfaceInfoOutsideService;

    public String geneAStr() {
        // 接口调用计数
        try {
            userInterfaceInfoOutsideService.invokeCountChange(
                    InterfaceDictionary.GET_A_LUCKY_STR.getInterfaceId(),
                    authUtils.getCurrUser(false).getAccount()
            );
        } catch (DomainException e) {
            throw new DomainException(e.getCode(), e.getMessage());
        }

        return "JustDOIt." + generateRandomString(10);
    }

    public IpInfoResp ipInfo(HttpServletRequest request) {
        try {
            // 接口调用计数
            userInterfaceInfoOutsideService.invokeCountChange(
                    InterfaceDictionary.GET_LOCAL_IP_INFO.getInterfaceId(),
                    authUtils.getCurrUser(false).getAccount()
            );

            // 1. 获取客户端真实IP（支持代理场景）
            String ip = resolveRealClientIp(request);

            // 2. 调用第三方API获取IP信息
            String apiUrl = String.format(IP_API_URL, ip);
            String respBody = HttpUtil.get(apiUrl);

            // 3. 提取有效JSON数据（处理回调函数包裹）
            String jsonData = extractJsonData(respBody).trim();

            // 4. 解析JSON并构建响应对象
            IpInfoResp ipInfoResp = objectMapper.readValue(jsonData, IpInfoResp.class);

            return ipInfoResp;
        } catch (DomainException e) {
            throw new DomainException(e.getCode(), e.getMessage());
        } catch (IOException e) {
            throw new DomainException(500, "IP信息查询服务异常: " + e.getMessage());
        }
    }

    public String uuidBatch(GeneUUIDReq req) {
        // 接口调用计数
        try {
            userInterfaceInfoOutsideService.invokeCountChange(
                    InterfaceDictionary.BATCH_GENE_UUID.getInterfaceId(),
                    authUtils.getCurrUser(false).getAccount()
            );
        } catch (DomainException e) {
            throw new DomainException(e.getCode(), e.getMessage());
        }

        List<String> uuids = IntStream.range(0, req.getCount())
                .mapToObj(i -> UUID.randomUUID().toString())
                .collect(Collectors.toList());

        return uuids.toString();
    }

    // ========== 私有工具方法 ==========

    /**
     * 解析客户端真实IP（支持代理/Nginx/CDN等场景）
     * 优先级：X-Real-IP > X-Forwarded-For > Proxy-Client-IP > WL-Proxy-Client-IP > RemoteAddr
     */
    private String resolveRealClientIp(HttpServletRequest request) {
        // 1. 检查常见代理服务器的头部（按优先级排序）
        String[] headersToCheck = {
                "X-Real-IP",
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                // 处理X-Forwarded-For的多IP情况（取第一个有效IP）
                if ("X-Forwarded-For".equals(header)) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 2. 直接获取远程地址（处理本地测试场景）
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "127.0.0.1".equals(remoteAddr)) {
            return getLocalMachineRealIp(); // 本地环境获取本机实际IP
        }
        return remoteAddr;
    }

    /**
     * 校验IP是否有效
     */
    private boolean isValidIp(String ip) {
        return !StrUtil.isBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取本机真实IP（仅在开发环境需要）
     */
    private String getLocalMachineRealIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1"; // 兜底
        }
    }

    /**
     * 提取JSON核心数据（处理回调函数包裹）
     */
    private String extractJsonData(String rawResponse) {
        final String PREFIX = "IPCallBack(";
        final String SUFFIX = ");";

        if (rawResponse.contains(PREFIX) && rawResponse.contains(SUFFIX)) {
            return StrUtil.subBetween(rawResponse, PREFIX, SUFFIX);
        }
        return rawResponse;
    }

    /**
     * 生成一个指定长度的随机字符串。
     *
     * @param length 随机字符串的长度
     * @return 随机生成的字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
