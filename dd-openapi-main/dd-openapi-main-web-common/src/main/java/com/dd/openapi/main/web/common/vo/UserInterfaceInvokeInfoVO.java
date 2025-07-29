package com.dd.openapi.main.web.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/29 11:20
 * @Description 用户对接口的调用统计信息
 */
@Data
@Builder
@Schema(description = "用户对接口的调用统计信息")
public class UserInterfaceInvokeInfoVO {
    @Schema(description = "针对当前用户的剩余调用次数")
    private Integer invokeLeftNum;
    @Schema(description = "当前用户已调用次数")
    private Integer invokedNum;
}
