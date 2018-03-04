package com.dataman.gitstats.exception;

/**
 * Created by biancl on 2017-09-22.
 * 返回码
 */
public enum ApiResultCode {
    SUCCESS("0000","操作成功"),
    ERR_NOT_AUTH("401","您未登录，请登录后操作"),
    ERR_AUTH_TIMEOUT("402","登录超时，请重新登录"),
    ERR_USERNAME_PASSWORD("1001","用户名密码错误"),
    ERR_PARAMETER("1002","参数错误:"),
    ERR_OLD_PASSWORD("1003","旧密码错误"),
    ERR_1001("1000","未知错误");
    private String code;
    private String message;
    ApiResultCode(String code, String message){
        this.code=code;
        this.message=message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
