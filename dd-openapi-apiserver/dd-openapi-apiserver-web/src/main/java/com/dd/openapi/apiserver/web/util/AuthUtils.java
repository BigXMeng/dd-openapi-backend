package com.dd.openapi.apiserver.web.util;

import com.dd.ms.auth.api.UserInfoService;
import com.dd.ms.auth.bo.UserInternalBO;
import com.dd.ms.auth.vo.UserVO;
import com.dd.openapi.common.api.auth.ApiAuthConstants;
import com.dd.openapi.common.exception.DomainException;
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
    public UserVO getCurrUser(Boolean permissionRequired) {
        // 1. 获取当前HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        // 2. 从请求头获取Authorization
        String authHeaderStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaderStr == null || authHeaderStr.isEmpty()) {
            return null;
        }

        // 3. 提取Token并调用用户服务
        //String token = authHeader.substring(7); // 主模块传递来的token已经去除前缀"Bearer"
        return userInfoService.getUserInfoByToken(authHeaderStr, permissionRequired);
    }

    public UserInternalBO getCurrUserBO() {
        // 1. 获取当前HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        // 2. 从请求头获取Authorization
        String apiAccessKey = request.getHeader(ApiAuthConstants.ACCESS_KEY_HEADER);
        if (apiAccessKey == null || apiAccessKey.isEmpty()) {
            return null;
        }

        // 3. 提取Token并调用用户服务
        return userInfoService.getUserInfoByAccessKey(apiAccessKey);
    }

    /**
     * 获取当前用户账户
     *
     * @return
     */
    public String getCurrUserAccount() {
        UserVO currUser = getCurrUser(false);
        if (currUser != null) {
            return currUser.getAccount();
        }
        UserInternalBO currUserBO = getCurrUserBO();
        if (currUserBO != null) {
            return currUserBO.getAccount();
        }
        throw new DomainException(401, "无法获取到用户信息");
    }
}
