package com.dd.openapi.main.web.util;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.ms.auth.vo.UserVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/25 16:08
 * @Description 类功能作用说明
 */
@Component
public class AuthUtils {

    @DubboReference(interfaceClass = UserInfoService.class, group = "DUBBO_DD_MS_AUTH", version = "1.0")
    private UserInfoService userInfoService;

    /**
     * 获取当前登录用户信息
     * @return UserVO 当前用户对象，未认证时返回null
     */
    public UserVO getCurrUser() {
        // 1. 获取当前HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        // 2. 从请求头获取Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        // 3. 提取Token并调用用户服务
        String token = authHeader.substring(7);
        return userInfoService.getUserInfoByToken(token);
    }
}
