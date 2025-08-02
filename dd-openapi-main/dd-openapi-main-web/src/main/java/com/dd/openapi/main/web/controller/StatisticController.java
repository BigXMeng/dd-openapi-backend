package com.dd.openapi.main.web.controller;

import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.main.web.common.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.model.vo.InterfaceInvokeTop3InfoVO;
import com.dd.openapi.main.web.service.internal.impl.StatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/31 23:13
 * @Description 接口调用统计
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistic")
@Api(tags = "接口调用统计")
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/top-3")
    @ApiOperation("接口调用数量top3")
    public ApiResponse<List<InterfaceInvokeTop3InfoVO>> topThree() {
        return ApiResponse.success(statisticService.interfaceInvokeTop3InfoVO());
    }
}
