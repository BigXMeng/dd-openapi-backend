package com.dd.openapi.main.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 18:38
 * @Description 接口信息视图对象
 */
@Data
@Schema(description = "接口信息视图对象")
public class InterfaceInfoVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "接口地址")
    private String url;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "请求头")
    private String requestHeader;

    @Schema(description = "响应头")
    private String responseHeader;

    @Schema(description = "接口状态（0-关闭，1-开启）")
    private Integer status;

    @Schema(description = "请求类型")
    private String method;

    @Schema(description = "创建人ID")
    private String userAccount;

    @Schema(description = "创建人名称")
    private String userName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;
}
