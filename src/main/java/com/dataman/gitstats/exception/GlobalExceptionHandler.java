package com.dataman.gitstats.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * Created by biancl on 2017-09-22.
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private  Logger logger= LoggerFactory.getLogger(this.getClass());



    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception exception){
        if(exception instanceof UndeclaredThrowableException){
            if(((UndeclaredThrowableException) exception).getUndeclaredThrowable() instanceof BusinessException){
                logger.error("业务异常：",exception);
                return handleBusinessException((BusinessException)((UndeclaredThrowableException) exception).getUndeclaredThrowable());
            }
        }
        if(exception instanceof BusinessException){
            logger.error("业务异常：",exception);
            return handleBusinessException((BusinessException) exception);
        }else{
            logger.error("未知异常：",exception);
            return new ResponseEntity(exception,HttpStatus.EXPECTATION_FAILED);
        }

    }

    private ResponseEntity handleBusinessException(BusinessException ex){
        if(ex.getResultCode().equals(ApiResultCode.ERR_AUTH_TIMEOUT.getCode())||ex.getResultCode().equals(ApiResultCode.ERR_NOT_AUTH.getCode())){
            return new ResponseEntity(ex,HttpStatus.UNAUTHORIZED);
        }else if(ex.getResultCode().equals(ApiResultCode.ERR_PARAMETER.getCode())){
            return new ResponseEntity(ex,HttpStatus.BAD_REQUEST);
        }else {
           return  new ResponseEntity(ex,HttpStatus.OK);
        }
    }


}
