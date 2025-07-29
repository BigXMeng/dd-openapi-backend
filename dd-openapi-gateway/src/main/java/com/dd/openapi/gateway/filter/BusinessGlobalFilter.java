package com.dd.openapi.gateway.filter;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.openapi.gateway.exception.DomainException;
import com.dd.openapi.main.web.common.api.ExternalInterfaceInfoService;
import com.dd.openapi.main.web.common.api.ExternalUserInterfaceInfoService;
import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 13:58
 * @Description 业务全局过滤器
 */
@Slf4j
@Component
public class BusinessGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private UserInfoService userInfoService;

    @DubboReference
    private ExternalInterfaceInfoService externalInterfaceInfoService;

    @DubboReference
    private ExternalUserInterfaceInfoService externalUserInterfaceInfoService;

    // 定义 IP 白名单，只有白名单中的 IP 地址可以访问
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    // 模拟接口的主机地址
    private static final String INTERFACE_HOST = "http://localhost:18012";

    /**
     * 实现 GlobalFilter 的 filter 方法，处理每个请求的过滤逻辑。
     * 主要步骤包括：
     * 1. 记录请求日志
     * 2. 检查 IP 白名单
     * 3. 用户鉴权（暂未实现）
     * 4. 检查接口是否存在及方法是否匹配
     * 5. 请求转发并处理响应
     *
     * @param exchange 当前请求的上下文
     * @param chain    过滤链，用于调用下一个过滤器
     * @return 返回一个 Mono<Void>，表示过滤操作的完成
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest(); // 获取当前请求
        ServerHttpResponse response = exchange.getResponse(); // 获取当前响应

        // 1. 记录请求日志
        logRequest(request);

        // 2. 检查 IP 白名单
        if (!isIpAllowed(request)) {
            return handleNoAuth(response); // 如果 IP 不在白名单中，返回 403 Forbidden
        }

        // 3. TODO 用户鉴权（目前在apiserver模块实现）
        if (!authenticateUser(request)) {
            return handleNoAuth(response); // 如果鉴权失败，返回 403 Forbidden
        }

        // 4. 检查接口是否存在及方法是否匹配
        InterfaceInfoVO interfaceInfo = getInterfaceInfo(request);
        if (interfaceInfo == null) {
            return handleNoAuth(response); // 如果接口不存在或方法不匹配，返回 403 Forbidden
        }

        // 5. 请求转发并处理响应
        return handleResponse(exchange, chain, interfaceInfo.getId(), interfaceInfo.getUserAccount());
    }

    /**
     * 记录请求的基本信息，包括请求路径、方法、参数、来源地址等。
     * 这些信息对于调试和监控非常有用。
     *
     * @param request 当前请求
     */
    private void logRequest(ServerHttpRequest request) {
        String path = INTERFACE_HOST + request.getPath().value(); // 获取完整的请求路径
        String method = request.getMethod().toString(); // 获取请求方法（GET、POST 等）
        String sourceAddress = request.getLocalAddress().getHostString(); // 获取请求来源地址
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
    }

    /**
     * 检查请求来源的 IP 地址是否在白名单中。
     * 如果不在白名单中，直接拒绝访问。
     *
     * @param request 当前请求
     * @return 如果 IP 在白名单中返回 true，否则返回 false
     */
    private boolean isIpAllowed(ServerHttpRequest request) {
        String sourceAddress = request.getLocalAddress().getHostString(); // 获取请求来源地址
        return IP_WHITE_LIST.contains(sourceAddress); // 检查是否在白名单中
    }

    /**
     * 预留用户鉴权逻辑。
     * 目前返回 true，表示鉴权通过。
     * 后续需要实现具体的鉴权逻辑，例如检查 AK 和 SK 是否合法。
     *
     * @param request 当前请求
     * @return 如果鉴权通过返回 true，否则返回 false
     */
    private boolean authenticateUser(ServerHttpRequest request) {
        // TODO: 实现用户鉴权逻辑，例如检查 AK 和 SK 是否合法
        return true;
    }

    /**
     * 检查请求的接口是否存在，以及请求方法是否匹配。
     * 如果接口不存在或方法不匹配，返回 null。
     *
     * @param request 当前请求
     * @return 如果接口存在且方法匹配，返回接口信息；否则返回 null
     */
    private InterfaceInfoVO getInterfaceInfo(ServerHttpRequest request) {
        String path = INTERFACE_HOST + request.getPath().value(); // 获取完整的请求路径
        String method = request.getMethod().toString(); // 获取请求方法
        try {
            return externalInterfaceInfoService.getInterfaceInfo(path, method); // 调用服务获取接口信息
        } catch (Exception e) {
            log.error("获取接口信息失败", e); // 记录异常日志
        }
        return null;
    }

    /**
     * 处理请求的响应。
     * 主要功能包括：
     * 1. 装饰响应对象，以便在响应完成后记录日志。
     * 2. 增加接口调用次数。
     * 3. 返回最终的响应。
     *
     * @param exchange 当前请求的上下文
     * @param chain    过滤链
     * @param interfaceInfoId 接口信息的 ID
     * @param userAccount 用户账号
     * @return 返回一个 Mono<Void>，表示响应处理的完成
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, String userAccount) {
        ServerHttpResponse originalResponse = exchange.getResponse(); // 获取原始响应
        DataBufferFactory bufferFactory = originalResponse.bufferFactory(); // 获取数据缓冲区工厂
        HttpStatus statusCode = originalResponse.getStatusCode(); // 获取响应状态码

        // 如果响应状态码为 200 OK，进行响应处理
        if (statusCode == HttpStatus.OK) {
            return chain.filter(exchange.mutate().response(new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        return super.writeWith(Flux.from(body).map(dataBuffer -> {
                            try {
                                externalUserInterfaceInfoService.invokeCount(interfaceInfoId, userAccount); // 增加接口调用次数
                            } catch (Exception e) {
                                throw new DomainException(500, e.getMessage()); // 如果调用失败，抛出异常
                            }
                            byte[] content = new byte[dataBuffer.readableByteCount()]; // 读取响应内容
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer); // 释放内存
                            String data = new String(content, StandardCharsets.UTF_8); // 转换为字符串
                            log.info("响应结果：" + data); // 记录响应日志
                            return bufferFactory.wrap(content); // 返回新的数据缓冲区
                        }));
                    } else {
                        log.error("<--- {} 响应code异常", getStatusCode()); // 如果响应体不是 Flux 类型，记录异常日志
                    }
                    return super.writeWith(body); // 调用父类方法
                }
            }).build());
        } else {
            log.error("响应状态码异常：" + statusCode); // 如果响应状态码不是 200 OK，记录异常日志
        }
        return chain.filter(exchange); // 如果响应状态码不是 200 OK，直接返回原始响应
    }

    /**
     * 定义过滤器的顺序。
     * 数字越小，优先级越高。
     *
     * @return 返回过滤器的顺序值
     */
    @Override
    public int getOrder() {
        return -1; // 设置为最高优先级
    }

    /**
     * 处理未授权的请求。
     * 返回 403 Forbidden 状态码。
     *
     * @param response 当前响应
     * @return 返回一个 Mono<Void>，表示处理完成
     */
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN); // 设置响应状态码为 403 Forbidden
        return response.setComplete(); // 完成响应
    }
}