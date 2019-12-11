package com.otn.calculationroad.exception;

import com.otn.calculationroad.en.StateCode;
import com.otn.calculationroad.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-12-10 15:32
 * @Description: 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    /**
     * 自定义的参数解析异常
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = ParamterException.class)
    public Result handleParamterException(HttpServletRequest request,ParamterException exception){
        log.info("come in paramterException,errorMsg is {}",exception.getMessage());
        return Result.me().response(exception.getErrorCode(),exception.getMessage());
    }

}