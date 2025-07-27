package com.dd.openapi.main.web.config.intercepter;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.openapi.main.web.config.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 11:22
 * @Description 权限拦截器
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的 accessKey 和 secretKey
        String token = request.getHeader("Authorization");
        try {
            //userInfoService.getUserInfoByToken(token.substring(7));
        } catch (Exception e) {
            throw new DomainException(401, "您尚未登陆");
        }
        return true;
    }
}