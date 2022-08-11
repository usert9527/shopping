package com.user9527.common.exception;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/3/27 20:11
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "用户已存在异常"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已存在异常"),
    LOGINACCT_PASSWORD_INVAILD_EXCEPTION(15003, "账号/密码错误");
    private int code;
    private String msg;

    BizCodeEnum(int code, String msg){
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
