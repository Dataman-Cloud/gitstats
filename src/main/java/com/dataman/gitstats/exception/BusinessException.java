package com.dataman.gitstats.exception;

/**
 * Created by biancl on 2017-09-22.
 * 业务异常类
 */
public class BusinessException extends Exception{

    private String resultCode;
    private String data;

    public BusinessException(ApiResultCode apiResultCode){
        this(apiResultCode.getCode(),apiResultCode.getMessage());
    }

    public BusinessException(String resultCode,String message){
        super(message);
        this.resultCode=resultCode;
        this.data=message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
