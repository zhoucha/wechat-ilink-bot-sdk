package com.wechat.ilink.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 二维码状态响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrcodeStatusResponse extends ApiResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("bot_token")
    private String botToken;

    @JsonProperty("ilink_bot_id")
    private String ilinkBotId;

    @JsonProperty("baseurl")
    private String baseUrl;

    @JsonProperty("ilink_user_id")
    private String ilinkUserId;

    public QrcodeStatusResponse() {
    }

    public QrcodeStatus getStatusEnum() {
        return QrcodeStatus.fromCode(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getIlinkBotId() {
        return ilinkBotId;
    }

    public void setIlinkBotId(String ilinkBotId) {
        this.ilinkBotId = ilinkBotId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getIlinkUserId() {
        return ilinkUserId;
    }

    public void setIlinkUserId(String ilinkUserId) {
        this.ilinkUserId = ilinkUserId;
    }

    @Override
    public String toString() {
        return "QrcodeStatusResponse{status='" + status + "', botId='" + ilinkBotId + "', success=" + isSuccess() + "}";
    }
}
