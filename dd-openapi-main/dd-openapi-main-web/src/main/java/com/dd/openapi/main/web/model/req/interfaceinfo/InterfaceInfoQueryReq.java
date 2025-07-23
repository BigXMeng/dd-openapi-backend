package com.dd.openapi.main.web.model.req.interfaceinfo;

import com.dd.openapi.common.param.PageParams;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "接口信息分页查询请求")
public class InterfaceInfoQueryReq {
    @Schema(description = "接口信息分页查询参数")
    private PageParams pageParams;
    @Schema(description = "接口信息分页查询参数")
    private InterfaceInfoQueryParams queryParams;
}
