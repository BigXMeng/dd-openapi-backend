package com.dd.openapi.main.web.model.req.interfaceinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:57
 * @Description 接口信息分页查询参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接口信息分页查询参数")
public class InterfaceInfoQueryParams {

    @ApiModelProperty(value = "ID列表", required = true, example = "[1,2,3]")
    @NotEmpty(message = "ID列表不能为空")
    private List<Long> ids;

    @ApiModelProperty(value = "接口名称", example = "用户")
    private String name;

    @ApiModelProperty(value = "接口描述", example = "查询")
    private String description;

    @ApiModelProperty(value = "接口地址", example = "/api/user")
    private String url;

    @ApiModelProperty(value = "请求类型", example = "GET")
    private String method;

    @ApiModelProperty(value = "创建人ID", example = "123")
    private Long userId;

    @ApiModelProperty(value = "接口状态（0-关闭，1-开启）", example = "1")
    private Integer status;

    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)", example = "0")
    private Integer isDelete;

    @ApiModelProperty(value = "创建时间范围", example = "[\"2023-01-01\", \"2023-12-31\"]")
    private List<String> createTimeRange;

    @ApiModelProperty(value = "更新时间范围", example = "[\"2023-06-01\", \"2023-12-31\"]")
    private List<String> updateTimeRange;
}
