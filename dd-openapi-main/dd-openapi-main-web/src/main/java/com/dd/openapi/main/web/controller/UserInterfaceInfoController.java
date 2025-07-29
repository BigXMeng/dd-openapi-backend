package com.dd.openapi.main.web.controller;

import com.dd.openapi.common.response.ApiResponse;
import com.dd.openapi.main.web.model.req.userinterface.EnableInvokeInterfaceReq;
import com.dd.openapi.main.web.service.internal.UserInterfaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户调用接口关系 前端控制器
 * </p>
 *
 * @author liuxianmeng
 * @since 2025-07-28
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-interface")
@Api(tags = "接口信息管理")
public class UserInterfaceInfoController {

    private final UserInterfaceInfoService userInterfaceInfoService;

    @PostMapping("/enable-invoke")
    @ApiOperation("开通调用次数")
    public ApiResponse<Void> enableInvoke(@ApiParam("接口信息添加请求") @RequestBody EnableInvokeInterfaceReq req) {
        log.info("C UserInterfaceInfoController M enableInvoke() req = {}", req);
        userInterfaceInfoService.enableInvoke(req);
        return ApiResponse.success();
    }
}
