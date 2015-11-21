package com.baidupanapi.exception.base;

/**
 * 因为帐号原因引起的登录失败异常
 * 如果是超时则是返回Timeout的异常
 *
 * Created by xm on 15-11-19.
 */

public class LoginFailedException extends RuntimeException{

    public LoginFailedException() {
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
