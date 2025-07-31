package com.dd.openapi.apiserver.common.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 11:08
 * @Description 调用生成UUID字符串列表API 响应数据
 */
@Data
@NoArgsConstructor
@ApiModel(description = "调用生成UUID字符串列表API 响应数据")
public class CallUUIDGeneResp implements Serializable {

    @ApiModelProperty(
            value = "调用生成UUID字符串列表API 响应数据"
    )
    @JsonProperty("uuidList") // 明确指定 JSON 字段名称
    List<String> uuidList;
}
