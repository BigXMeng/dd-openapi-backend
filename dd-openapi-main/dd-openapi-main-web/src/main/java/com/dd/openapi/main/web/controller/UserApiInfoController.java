package com.dd.openapi.main.web.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/user-api")
@Api(tags = "接口信息管理")
public class UserApiInfoController {

}
