package com.niujl.common;

/**
 * 统一业务错误码。
 */
public enum ResultCode {

    SUCCESS(0, "success"),

    PARAM_ERROR(1001, "参数错误"),
    SMS_CODE_ERROR(1002, "验证码错误或已过期"),
    SMS_SEND_TOO_FREQUENT(1003, "验证码发送过于频繁，请稍后再试"),
    PHONE_ALREADY_BOUND(1004, "该手机号已被绑定"),

    UNAUTHORIZED(1401, "未登录或登录已过期"),
    TOKEN_INVALID(1402, "token 无效"),

    USER_NOT_FOUND(1404, "用户不存在"),
    USER_CANCELLED(1405, "账号已注销，无法登录"),

    THIRD_PARTY_ERROR(1601, "第三方登录失败"),

    SERVER_ERROR(1500, "服务器内部错误");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
