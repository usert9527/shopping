package com.user9527.shopping.member.exception;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/18 9:28
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已经存在");
    }
}
