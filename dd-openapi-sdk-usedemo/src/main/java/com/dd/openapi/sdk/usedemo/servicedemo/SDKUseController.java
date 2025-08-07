package com.dd.openapi.sdk.usedemo.servicedemo;

import com.dd.openapi.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/8/7 14:18
 * @Description 类功能作用说明
 */
@RestController
@RequestMapping("/sdk-use")
public class SDKUseController {

    @Autowired
    private SDKUseService sdkUseService;

    @GetMapping("/gene-str-rst")
    public ApiResponse<String> geneStrRst() {
        return sdkUseService.geneAStr();
    }
}
