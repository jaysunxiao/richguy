package com.richguy.model.result;

/**
 * HTTP状态码规范，英文名称，code，message都符合标准规范
 * 1xx：相关信息
 * 2xx：操作成功
 * 3xx：重定向
 * 4xx：客户端错误
 * 5xx：服务器错误
 */
public enum CodeEnum {

    /**
     * 通用错误码
     */
    FAIL(0, "请求失败"),
    OK(1, "请求成功"),

    PARAMETER_ERROR(10, "请求参数非法"),
    PARAMETER_ENV_ERROR(26, "为了安全，此zfoo环境暂时屏蔽热更新，你自己的正式环境中可以打开"),

    /**
     * 500-600为服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理"),

    /**
     * 1000-1100为登录相关
     */
    SIGN_IN_FAIL(1000, "账号密码错误"),

    ;

    private final int code;
    private final String message;

    CodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
