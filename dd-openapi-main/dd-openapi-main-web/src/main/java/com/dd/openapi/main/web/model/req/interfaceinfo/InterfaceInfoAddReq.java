package com.dd.openapi.main.web.model.req.interfaceinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/7/21 19:05
 * @Description 接口信息添加请求
 */
@Data
@Schema(description = "接口信息添加请求")
public class InterfaceInfoAddReq {

    @Schema(description = "名称", required = true, example = "用户查询接口")
    @NotBlank(message = "名称不能为空")
    @Size(max = 256, message = "名称长度不能超过256个字符")
    private String name;

    @Schema(description = "描述", example = "用于查询用户信息的接口")
    @Size(max = 256, message = "描述长度不能超过256个字符")
    private String description;

    @Schema(description = "接口地址", required = true, example = "/api/user/{id}")
    @NotBlank(message = "接口地址不能为空")
    @Size(max = 512, message = "接口地址长度不能超过512个字符")
    private String url;

    @Schema(description = "请求参数", required = true, example = "{\"id\":\"long\"}")
    @NotBlank(message = "请求参数不能为空")
    private String requestParams;

    @Schema(description = "请求头", example = "{\"Content-Type\":\"application/json\"}")
    private String requestHeader;

    @Schema(description = "响应头", example = "{\"Content-Type\":\"application/json\"}")
    private String responseHeader;

    @Schema(description = "接口状态（0-关闭，1-开启）", example = "1")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    private Integer status;

    @Schema(description = "请求类型", required = true, example = "GET")
    @NotBlank(message = "请求类型不能为空")
    @Pattern(regexp = "GET|POST|PUT|DELETE|PATCH", message = "请求类型必须是GET/POST/PUT/DELETE/PATCH")
    private String method;

    @Schema(description = "创建人账户名", required = true, example = "123")
    @NotNull(message = "创建人账户名不能为空")
    private String userAccount;
}
