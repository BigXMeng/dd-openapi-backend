package com.dd.openapi.apiserver.common.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/24 9:56
 * @Description 获取本地IP地址解析信息 响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("获取本地IP地址解析信息 响应类")
@JsonIgnoreProperties(ignoreUnknown = true) // 安全防护
public class IpInfoResp implements Serializable {
    @ApiModelProperty("客户端IP地址")
    private String ip;
    @ApiModelProperty("省份")
    private String pro;
    @ApiModelProperty("省份编码")
    private String proCode;
    @ApiModelProperty("城市")
    private String city;
    @ApiModelProperty("城市编码")
    private String cityCode;
    @ApiModelProperty("地域信息")
    private String region;  // 新增字段
    @ApiModelProperty("地域编码")
    private String regionCode;
    @ApiModelProperty("详细地址")
    private String addr;
    @ApiModelProperty("地域名称")
    private String regionNames;
    @ApiModelProperty("错误信息")
    private String err;
}

