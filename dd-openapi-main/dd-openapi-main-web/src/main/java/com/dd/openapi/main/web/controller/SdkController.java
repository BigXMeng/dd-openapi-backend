package com.dd.openapi.main.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/28 14:12
 * @Description SDK服务
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sdk")
@Api(tags = "SDK服务")
public class SdkController {

    @GetMapping("/download")
    @ApiOperation("下载开放API对应的SDK")
    public ResponseEntity<Resource> downloadSdk() throws IOException {

        // 从 classpath:static/ 加载文件
        Resource resource = new ClassPathResource("static/dd-openapi-sdk-1.0-SNAPSHOT.jar");

        // 检查文件是否存在
        if (!resource.exists()) {
            throw new RuntimeException("File not found: dd-openapi-sdk-1.0-SNAPSHOT.jar");
        }

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dd-openapi-sdk-1.0-SNAPSHOT.jar");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
