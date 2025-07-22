package com.dd.openapi.main.web.model.req.interfaceinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:05
 * @Description 接口信息删除请求
 */
@Data
@ApiModel("接口信息删除请求")
public class InterfaceInfoDeleteReq {
    @ApiModelProperty(value = "接口id列表", required = true, example = "[1,34,2]")
    @NotNull(message = "接口id列表不能为空")
    private List<Long> ids;
}
