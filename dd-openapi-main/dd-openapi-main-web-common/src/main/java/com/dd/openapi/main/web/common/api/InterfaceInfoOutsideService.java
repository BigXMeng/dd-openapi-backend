package com.dd.openapi.main.web.common.api;

import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/28 11:18
 * @Description 内部接口信息服务
 */
public interface InterfaceInfoOutsideService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfoVO getInterfaceInfo(String path, String method);
}
