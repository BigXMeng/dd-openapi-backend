package com.dd.openapi.main.web.model.req.userinterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:05
 * @Description 开通接口调用次数
 */
@Data
@Schema(description = "开通接口调用次数")
public class EnableInvokeInterfaceReq {

    @Schema(description = "接口id（必须传值）")
    @NotBlank(message = "接口id不能为空")
    private Long interfaceId;

    @Schema(description = "调用次数", example = "1")
    @NotNull(message = "调用次数不能为空")
    private Integer invokeNum;
}
