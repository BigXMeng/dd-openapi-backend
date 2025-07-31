package com.dd.openapi.common.response;

import lombok.Getter;

/**
 @author bigbigmeng
 @createTime 2024/11/5 16:36
 @instruction 返回信息枚举
 */
@Getter
public enum ApiResponseEnum {

    /********************** 通用响应枚举 *******************/
    SUCCESS(200, "成功"),
    FAIL(500, "服务端错误"),

    /********************** 权限响应枚举 *******************/
    FORBIDDEN_INVOCATION_PERMISSION_HAS_NOT_BEEN_ENABLED(403001, "当前用户暂未申请此接口调用额度，请申请调用额度。"),
    FORBIDDEN_QUOTA_FOR_INTERFACE_CALLS_HAS_BEEN_EXHAUSTED(403002, "当前用户此接口调用额度已用完，请再次申请调用额度。");

    private int code;
    private String message;

    ApiResponseEnum(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
