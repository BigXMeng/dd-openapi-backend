package com.dd.openapi.apiserver.common.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/24 9:57
 * @Description 类功能作用说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResp implements Serializable {
    @ApiModelProperty("Base64编码的图片数据（含MIME类型）")
    private String base64Image;
}
