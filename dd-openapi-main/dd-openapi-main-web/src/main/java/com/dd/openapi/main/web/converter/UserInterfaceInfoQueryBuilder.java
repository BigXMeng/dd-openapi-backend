package com.dd.openapi.main.web.converter;

import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryReq;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:12
 * @Description 类功能作用说明
 */
public class UserInterfaceInfoQueryBuilder {

    public static Map<String, Object>  buildQueryConditions(InterfaceInfoQueryReq req) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", req.getQueryParams().getStatus());
        params.put("description", req.getQueryParams().getDescription());
        params.put("name", req.getQueryParams().getName());
        params.put("userAccount", req.getQueryParams().getUserAccount());
        params.put("url", req.getQueryParams().getUrl());
        params.put("method", req.getQueryParams().getMethod());
        return params;
    }
}