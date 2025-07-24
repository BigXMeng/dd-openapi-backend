package com.dd.openapi.apiserver.common.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/24 9:58
 * @Description 类功能作用说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonDiffResp {
    @ApiModelProperty("是否完全一致")
    private boolean same;
    @ApiModelProperty("差异描述（RFC 6902格式）")
    private String diffText;
}
