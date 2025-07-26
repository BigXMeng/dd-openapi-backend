package com.dd.openapi.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author liuxianmeng
 * @CreateTime 2025/6/2 16:45
 * @Instruction 统一返回相应类 这个响应类只返回成功请求的情况 出现异常的情况交给全局异常处理器
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("统一信息返回类")
public class ApiResponse<T> implements Serializable {
    @ApiModelProperty("状态码")
    int code;
    @ApiModelProperty("提示信息")
    String message;
    @ApiModelProperty("响应头（JSON）")
    String responseHeader;
    @ApiModelProperty("响应时间（ms毫秒）")
    String responseTime;
    @ApiModelProperty("数据")
    T data;

    /**
     * 分别传入code和message的异常响应
     */
    public static ApiResponse<?> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, null, null);
    }

    /**
     * 传入ResultEnum枚举和响应体的成功响应
     */
    public static <T> ApiResponse<T> error(ApiResponseEnum apiResponseEnum) {
        return new ApiResponse<>(apiResponseEnum.getCode(), apiResponseEnum.getMessage(), null, null,null);
    }

    /**
     * 不带数据的异常返回
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ApiResponseEnum.FAIL.getCode(), message, null, null, null);
    }

    /**
     * 不带数据的操作成功响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ApiResponseEnum.SUCCESS.getCode(), ApiResponseEnum.SUCCESS.getMessage(), null, null, null);
    }

    /**
     * 传入ResultEnum枚举和响应体的成功响应
     */
    public static <T> ApiResponse<T> success(ApiResponseEnum apiResponseEnum, T t) {
        return new ApiResponse<>(apiResponseEnum.getCode(), apiResponseEnum.getMessage(), null, null, t);
    }

    /**
     * 只传入响应体的成功响应
     */
    public static <T> ApiResponse<T> success(T t) {
        return new ApiResponse<>(ApiResponseEnum.SUCCESS.getCode(), ApiResponseEnum.SUCCESS.getMessage(), null, null, t);
    }
}
