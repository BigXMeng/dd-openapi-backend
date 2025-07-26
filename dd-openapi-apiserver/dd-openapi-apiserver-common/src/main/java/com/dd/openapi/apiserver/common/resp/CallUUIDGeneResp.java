package com.dd.openapi.apiserver.common.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/26 11:08
 * @Description 调用生成UUID字符串列表API 响应数据
 */
@Data
@ApiModel(description = "调用生成UUID字符串列表API 响应数据")
public class CallUUIDGeneResp {

    @ApiModelProperty(
            value = "调用生成UUID字符串列表API 响应数据"
    )
    ArrayList<String> uuidList;
}
