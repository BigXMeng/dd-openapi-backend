package com.dd.openapi.main.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/1/9 1:15
 * @Description 分页请求类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("分页请求类")
public class PageParams {
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;
    @ApiModelProperty(value = "页大小", example = "10")
    private Integer pageSize = 10;
}
