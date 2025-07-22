package com.dd.openapi.main.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:38
 * @Description 接口信息视图对象
 */
@Data
@ApiModel("接口信息视图对象")
public class InterfaceInfoVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("接口地址")
    private String url;

    @ApiModelProperty("请求参数")
    private String requestParams;

    @ApiModelProperty("请求头")
    private String requestHeader;

    @ApiModelProperty("响应头")
    private String responseHeader;

    @ApiModelProperty("接口状态（0-关闭，1-开启）")
    private Integer status;

    @ApiModelProperty("请求类型")
    private String method;

    @ApiModelProperty("创建人ID")
    private String userAccount;

    @ApiModelProperty("创建人名称")
    private String userName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("是否删除(0-未删, 1-已删)")
    private Integer isDelete;
}
