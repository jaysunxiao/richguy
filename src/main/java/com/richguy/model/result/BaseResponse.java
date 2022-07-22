package com.richguy.model.result;

import org.springframework.lang.Nullable;

/**
 * http请求的通用返回
 */
public class BaseResponse {

    private int code;
    private String message;
    private Object data;

    public static BaseResponse valueOf(int code) {
        var response = new BaseResponse();
        response.code = code;
        return response;
    }

    public static BaseResponse valueOf(CodeEnum code) {
        var response = new BaseResponse();
        response.code = code.getCode();
        response.message = code.getMessage();
        return response;
    }

    public static BaseResponse valueOf(CodeEnum code, @Nullable Object data) {
        var response = new BaseResponse();
        response.code = code.getCode();
        response.message = code.getMessage();
        response.data = data;
        return response;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
