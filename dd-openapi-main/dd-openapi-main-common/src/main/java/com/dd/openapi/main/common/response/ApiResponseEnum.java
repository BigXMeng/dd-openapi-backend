package com.dd.openapi.main.common.response;

import lombok.Getter;

/**
 @author bigbigmeng
 @createTime 2024/11/5 16:36
 @instruction 返回信息枚举
 */
@Getter
public enum ApiResponseEnum {
    /********************** 通用枚举 START *******************/
    SUCCESS(200, "成功"),
    FAIL(500, "服务端错误");
    /********************** 通用枚举 END *******************/

    private int code;
    private String message;

    ApiResponseEnum(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
