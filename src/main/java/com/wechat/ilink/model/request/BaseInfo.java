package com.wechat.ilink.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 基础信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseInfo {

    @JsonProperty("channel_version")
    private String channelVersion;

    public BaseInfo() {
        this.channelVersion = "0.1.0";
    }

    public BaseInfo(String channelVersion) {
        this.channelVersion = channelVersion;
    }

    public String getChannelVersion() {
        return channelVersion;
    }

    public void setChannelVersion(String channelVersion) {
        this.channelVersion = channelVersion;
    }

    @Override
    public String toString() {
        return "BaseInfo{channelVersion='" + channelVersion + "'}";
    }
}
