package com.wechat.ilink.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 获取消息请求
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUpdatesRequest {

    @JsonProperty("get_updates_buf")
    private String getUpdatesBuf;

    @JsonProperty("base_info")
    private BaseInfo baseInfo;

    public GetUpdatesRequest() {
        this.baseInfo = new BaseInfo();
    }


    public GetUpdatesRequest(String getUpdatesBuf) {
        this();
        this.getUpdatesBuf = getUpdatesBuf;
    }

    public GetUpdatesRequest(String getUpdatesBuf, String channelVersion) {
        this.getUpdatesBuf = getUpdatesBuf;
        this.baseInfo = new BaseInfo(channelVersion);
    }

    public String getGetUpdatesBuf() {
        return getUpdatesBuf;
    }

    public void setGetUpdatesBuf(String getUpdatesBuf) {
        this.getUpdatesBuf = getUpdatesBuf;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    @Override
    public String toString() {
        return "GetUpdatesRequest{getUpdatesBuf='" + getUpdatesBuf + "'}";
    }
}
