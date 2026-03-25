package com.wechat.ilink.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wechat.ilink.model.message.WechatMessage;

import java.util.List;

/**
 * 获取消息响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUpdatesResponse extends ApiResponse {

    @JsonProperty("msgs")
    private List<WechatMessage> msgs;

    @JsonProperty("get_updates_buf")
    private String getUpdatesBuf;

    @JsonProperty("longpolling_timeout_ms")
    private Long longpollingTimeoutMs;

    public GetUpdatesResponse() {
    }

    public List<WechatMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<WechatMessage> msgs) {
        this.msgs = msgs;
    }

    public String getGetUpdatesBuf() {
        return getUpdatesBuf;
    }

    public void setGetUpdatesBuf(String getUpdatesBuf) {
        this.getUpdatesBuf = getUpdatesBuf;
    }

    public Long getLongpollingTimeoutMs() {
        return longpollingTimeoutMs;
    }

    public void setLongpollingTimeoutMs(Long longpollingTimeoutMs) {
        this.longpollingTimeoutMs = longpollingTimeoutMs;
    }

    public boolean hasMessages() {
        return msgs != null && !msgs.isEmpty();
    }

    @Override
    public String toString() {
        int msgCount = msgs != null ? msgs.size() : 0;
        return "GetUpdatesResponse{msgCount=" + msgCount + ", buf='" +
                (getUpdatesBuf != null ? getUpdatesBuf.substring(0, Math.min(20, getUpdatesBuf.length())) + "..." : null) +
                "', success=" + isSuccess() + "}";
    }
}
