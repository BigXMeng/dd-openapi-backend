package com.dd.openapi.apiserver.common.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/24 9:57
 * @Description 类功能作用说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonDiffReq {
    @ApiModelProperty("左侧JSON字符串")
    private String left;
    @ApiModelProperty("右侧JSON字符串")
    private String right;
}
