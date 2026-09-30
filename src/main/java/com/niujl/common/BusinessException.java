package com.niujl.common;

/**
 * 业务异常，由全局异常处理器统一转换为 {@link Result}。
 */
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String msg) {
        super(msg);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
