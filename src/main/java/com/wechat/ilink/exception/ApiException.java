package com.wechat.ilink.exception;

/**
 * API 调用异常
 */
public class ApiException extends WechatIlinkException {

    private final Integer errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ApiException(String message, Integer errorCode) {
        super(message + " (errorCode=" + errorCode + ")");
        this.errorCode = errorCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
