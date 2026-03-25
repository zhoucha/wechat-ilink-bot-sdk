package com.wechat.ilink.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 二维码状态枚举
 */
public enum QrcodeStatus {
    WAIT("wait", "等待扫码"),
    SCANED("scaned", "已扫码，等待确认"),
    CONFIRMED("confirmed", "已确认"),
    EXPIRED("expired", "已过期");

    private final String code;
    private final String description;

    QrcodeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static QrcodeStatus fromCode(String code) {
        for (QrcodeStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public boolean isFinal() {
        return this == CONFIRMED || this == EXPIRED;
    }
}
