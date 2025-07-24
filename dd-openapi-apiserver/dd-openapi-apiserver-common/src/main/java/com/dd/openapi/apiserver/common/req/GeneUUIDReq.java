package com.dd.openapi.apiserver.common.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/24 13:14
 * @Description 类功能作用说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneUUIDReq {
    @ApiModelProperty("生成UUID的个数")
    private Integer count;
}
