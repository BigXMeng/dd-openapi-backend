package com.dd.openapi.gateway.filter;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.ms.auth.vo.UserVO;
import com.dd.openapi.common.redis.RedisKeyUtil;
import com.dd.openapi.gateway.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/30 18:14
 * @Description 网关过滤器
 */
@Slf4j
@Component
public class AdvancedGatewayFilter implements GlobalFilter, Ordered {

    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TRACE_ID = "X-Trace-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 统一鉴权
        RequestPath path = exchange.getRequest().getPath();
        log.info("当前请求path = " + path);
        // 1.1 如果是登陆请求 则不需要鉴权
        if(path.toString().equals("/gateway-api/auth-service/auth/login")
        || path.toString().equals("/gateway-api/auth-service/auth/register")) {
            return chain.filter(exchange);
        }
        // 1.2 获取Authorization请求头
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(token == null) {
            throw new DomainException(403, "您尚未登陆");
        }
        // 1.3 根据Authorization获取用户信息 使用Redis做缓存鉴权成功状态 避免频繁鉴权请求数据库
        String userAuthRedisKey = RedisKeyUtil.getUserAuthRedisKey(token);
        Object authMark = redisTemplate.opsForValue().get(userAuthRedisKey);
        if(authMark == null) {
            UserVO userInfoByToken;
            try {
                userInfoByToken = userInfoService.getUserInfoByToken(token.substring(7), false);
                if (userInfoByToken == null) {
                    throw new DomainException(401, "未获取到您的用户信息");
                }
                // redisTemplate.opsForValue().set(userAuthRedisKey, "PASS", Duration.ofMinutes(30));
            } catch (RuntimeException e) { // 捕获 Dubbo 可能抛出的 RuntimeException
                Throwable cause = e;
                while (cause != null) {
                    if (cause instanceof DomainException) {
                        DomainException domainEx = (DomainException) cause;
                        if (domainEx.getCode() == 401001) {
                            throw new DomainException(401001, "当前的accessToken已过期");
                        }
                        break;
                    }
                    cause = cause.getCause();
                }
                // 如果没有找到 DomainException，可以继续处理其他 RuntimeException
                System.out.println("...");
            }
        }

        // 2. 请求预处理：生成TraceID并添加到MDC/Header
        String traceId = generateTraceId(exchange);
        MDC.put("traceId", traceId);

        // 3. 详细日志打印：记录请求信息
        // 5. 在请求开始时设置时间戳（Long类型）

        return chain.filter(exchange).doFinally(signal -> MDC.clear());
    }

    private String generateTraceId(ServerWebExchange exchange) {
        // 优先使用上游传递的TraceID，否则生成新的
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(TRACE_ID))
                .orElse(UUID.randomUUID().toString());
    }

    @Override
    public int getOrder() {
        // 设置为最高优先级（最小数值）
        return Ordered.HIGHEST_PRECEDENCE;
    }
}