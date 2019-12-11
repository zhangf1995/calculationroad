package com.otn.calculationroad.exception;

import lombok.Data;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-12-10 15:45
 * @Description: 参数异常
 */
@Data
public class ParamterException extends RuntimeException {

    private Integer errorCode;

    public ParamterException(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public ParamterException(String message) {
        super(message);
    }

    public ParamterException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ParamterException(String message, Throwable cause, Integer errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ParamterException(Throwable cause, Integer errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ParamterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}