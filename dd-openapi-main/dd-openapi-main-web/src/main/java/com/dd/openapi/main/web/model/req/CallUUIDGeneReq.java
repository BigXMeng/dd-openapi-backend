package com.dd.openapi.main.web.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 10:49
 * @Description 调用生成UUID字符串列表API 请求类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "调用生成UUID字符串列表API 请求类")
public class CallUUIDGeneReq {

    @Schema(
            description = "生成的UUID字符串的数量",
            example = "8"
    )
    private Integer num;
}
