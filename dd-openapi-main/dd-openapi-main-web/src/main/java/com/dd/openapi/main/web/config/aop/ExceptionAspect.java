package com.dd.openapi.main.web.config.aop;

import com.dd.openapi.common.exception.DomainException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 12:29
 * @Description 异常处理切面类
 */
@Aspect
@Component
public class ExceptionAspect {

    /**
     * UI客户端调用API异常处理 切入所有Controller的public方法
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(public * com.dd.openapi.main.web.controller.InterfaceClientController..*.*(..))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();  // 执行原方法
        } catch (HttpServerErrorException e) {
            throw new DomainException(403, "您的API调用权限未开通或开通次数已用完");
        }
    }
}
