package com.user9527.shopping.member.exception;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/18 9:29
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名已经存在");
    }
}