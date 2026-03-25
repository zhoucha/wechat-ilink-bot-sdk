package com.wechat.ilink.exception;

/**
 * SDK 异常基类
 */
public class WechatIlinkException extends Exception {

    public WechatIlinkException(String message) {
        super(message);
    }

    public WechatIlinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
