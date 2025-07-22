package com.dd.openapi.main.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dd.openapi.main.common.response.ApiResponse;
import com.dd.openapi.main.web.model.DO.InterfaceInfoDO;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoAddReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoDeleteReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoQueryReq;
import com.dd.openapi.main.web.model.req.interfaceinfo.InterfaceInfoUpdateReq;
import com.dd.openapi.main.web.model.vo.InterfaceInfoVO;
import com.dd.openapi.main.web.service.InterfaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:40
 * @Description 接口信息管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/interface")
@Api(tags = "接口信息管理")
public class InterfaceInfoController {

    private final InterfaceInfoService interfaceInfoService;

    @PostMapping("/add")
    @ApiOperation("添加接口信息")
    public ApiResponse<Void> add(@ApiParam("接口信息添加请求") @RequestBody InterfaceInfoAddReq req) {
        log.info("C InterfaceInfoController M add() req = {}", req);
        interfaceInfoService.addOne(req);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除接口信息")
    public ApiResponse<Void> delete(@ApiParam("接口信息添加请求") @RequestBody InterfaceInfoDeleteReq req,
                                    HttpServletRequest request) {
        log.info("C InterfaceInfoController M delete() req = {}", req);
        // 获取用户token
        
        interfaceInfoService.delete(req);
        return ApiResponse.success();
    }

    @PatchMapping("/update")
    @ApiOperation("更新接口信息")
    public ApiResponse<Void> update(@ApiParam("接口信息更新请求") @RequestBody InterfaceInfoUpdateReq req) {
        log.info("C InterfaceInfoController M update() req = {}", req);
        interfaceInfoService.updateOne(req);
        return ApiResponse.success();
    }

    @PostMapping("/page")
    @ApiOperation("分页查询接口信息")
    public ApiResponse<IPage<InterfaceInfoVO>> page(@ApiParam("分页查询条件") @RequestBody InterfaceInfoQueryReq req) {
        log.info("C InterfaceInfoController M page() req = {}", req);
        return ApiResponse.success(interfaceInfoService.pageQuery(req));
    }

    @Deprecated
    @PostMapping("/test-data-gene")
    @ApiOperation("生成接口测试数据")
    public ApiResponse<Integer> generateInterfaceData(
            @ApiParam(value = "生成数量", required = true, example = "100")
            @RequestParam int count,
            @ApiParam(value = "用户ID范围", example = "1-500")
            @RequestParam(required = false) String userIdRange) {

        log.info("生成接口测试数据: count={}, userIdRange={}", count, userIdRange);
        int generatedCount = interfaceInfoService.generateInterfaceInfoData(count, userIdRange);
        return ApiResponse.success(generatedCount);
    }
}
