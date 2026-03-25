package com.wechat.ilink.exception;

/**
 * 认证异常
 */
public class AuthException extends WechatIlinkException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
