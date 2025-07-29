package com.dd.openapi.main.web.common.api;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/28 11:18
 * @Description 内部用户接口信息服务
 */
public interface ExternalUserInterfaceInfoService {

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口信息id
     * @param userAccount 用户账户id
     */
    void invokeCount(Long interfaceInfoId, String userAccount);
}
