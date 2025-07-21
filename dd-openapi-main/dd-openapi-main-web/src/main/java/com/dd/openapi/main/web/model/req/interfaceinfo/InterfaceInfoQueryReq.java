package com.dd.openapi.main.web.model.req.interfaceinfo;

import com.dd.openapi.main.common.param.PageParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:57
 * @Description 接口信息分页查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接口信息分页查询请求")
public class InterfaceInfoQueryReq {
    @ApiModelProperty(value = "接口信息分页查询参数")
    private PageParams pageParams;
    @ApiModelProperty(value = "接口信息分页查询参数")
    private InterfaceInfoQueryParams queryParams;
}
