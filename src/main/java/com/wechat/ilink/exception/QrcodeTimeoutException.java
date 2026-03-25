package com.wechat.ilink.exception;

/**
 * 扫码超时异常
 */
public class QrcodeTimeoutException extends WechatIlinkException {

    public QrcodeTimeoutException(String message) {
        super(message);
    }
}
