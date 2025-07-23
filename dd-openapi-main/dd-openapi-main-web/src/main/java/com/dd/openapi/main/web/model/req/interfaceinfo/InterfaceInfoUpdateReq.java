package com.dd.openapi.main.web.model.req.interfaceinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:03
 * @Description 接口信息更新请求
 */
@Data
@Schema(description = "接口信息更新请求")
public class InterfaceInfoUpdateReq {

    @Schema(description = "主键ID", required = true, example = "1")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "名称", example = "用户查询接口")
    @Size(max = 256, message = "名称长度不能超过256个字符")
    private String name;

    @Schema(description = "描述", example = "用于查询用户信息的接口")
    @Size(max = 256, message = "描述长度不能超过256个字符")
    private String description;

    @Schema(description = "接口地址", example = "/api/user/{id}")
    @Size(max = 512, message = "接口地址长度不能超过512个字符")
    private String url;

    @Schema(description = "请求参数", example = "{\"id\":\"long\",\"name\":\"string\"}")
    private String requestParams;

    @Schema(description = "请求头", example = "{\"Content-Type\":\"application/json\"}")
    private String requestHeader;

    @Schema(description = "响应头", example = "{\"Content-Type\":\"application/json\"}")
    private String responseHeader;

    @Schema(description = "接口状态（0-关闭，1-开启）", example = "1")
    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    private Integer status;

    @Schema(description = "请求类型", example = "GET")
    @Pattern(regexp = "GET|POST|PUT|DELETE|PATCH", message = "请求类型必须是GET/POST/PUT/DELETE/PATCH")
    private String method;

    @Schema(description = "是否删除(0-未删, 1-已删)", example = "0")
    @Range(min = 0, max = 1, message = "删除状态值只能是0或1")
    private Integer isDelete;
}
