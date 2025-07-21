package com.dd.openapi.main.web.converter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryParams;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:12
 * @Description 类功能作用说明
 */
public class InterfaceInfoQueryBuilder {

    public static LambdaQueryWrapper<InterfaceInfoDO> buildLQW(InterfaceInfoQueryParams params) {
        LambdaQueryWrapper<InterfaceInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (params == null) {
            return lambdaQueryWrapper;
        }

        // ID列表查询
        if (params.getIds() != null && !params.getIds().isEmpty()) {
            lambdaQueryWrapper.in(InterfaceInfoDO::getId, params.getIds());
        }

        // 名称模糊查询
        if (StringUtils.isNotBlank(params.getName())) {
            lambdaQueryWrapper.like(InterfaceInfoDO::getName, params.getName());
        }

        // 描述模糊查询
        if (StringUtils.isNotBlank(params.getDescription())) {
            lambdaQueryWrapper.like(InterfaceInfoDO::getDescription, params.getDescription());
        }

        // 方法类型查询
        if (StringUtils.isNotBlank(params.getMethod())) {
            lambdaQueryWrapper.eq(InterfaceInfoDO::getMethod, params.getMethod());
        }

        // 状态查询
        if (params.getStatus() != null) {
            lambdaQueryWrapper.eq(InterfaceInfoDO::getStatus, params.getStatus());
        }

        // 默认排序：按更新时间倒序
        lambdaQueryWrapper.orderByDesc(InterfaceInfoDO::getUpdateTime);

        return lambdaQueryWrapper;
    }
}