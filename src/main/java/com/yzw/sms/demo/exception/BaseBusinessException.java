package com.yzw.sms.demo.exception;

/**
 * @author YaoZuoWei
 * @date 2021/1/6 15:52
 * @Description:自定义异常
 */
public class BaseBusinessException extends RuntimeException{

    private Integer code;

    private String mseeage;

    public BaseBusinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
