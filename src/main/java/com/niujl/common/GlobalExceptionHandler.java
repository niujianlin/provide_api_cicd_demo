package com.niujl.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理：统一把异常转换为 {@link Result}。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getResultCode().getCode(), e.getMessage());
        return Result.fail(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return Result.fail(ResultCode.PARAM_ERROR, firstErrorMessage(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        return Result.fail(ResultCode.PARAM_ERROR, firstErrorMessage(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String msg = ResultCode.PARAM_ERROR.getMsg();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            msg = violation.getMessage();
            break;
        }
        return Result.fail(ResultCode.PARAM_ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ResultCode.SERVER_ERROR);
    }

    private String firstErrorMessage(FieldError fieldError) {
        return fieldError == null ? ResultCode.PARAM_ERROR.getMsg() : fieldError.getDefaultMessage();
    }
}
