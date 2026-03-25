package com.wechat.ilink.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wechat.ilink.model.message.WechatMessage;

/**
 * 发送消息请求
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessageRequest {

    @JsonProperty("msg")
    private WechatMessage msg;

    @JsonProperty("base_info")
    private BaseInfo baseInfo;

    public SendMessageRequest() {
        this.baseInfo = new BaseInfo();
    }

    public SendMessageRequest(WechatMessage msg) {
        this();
        this.msg = msg;
    }

    public WechatMessage getMsg() {
        return msg;
    }

    public void setMsg(WechatMessage msg) {
        this.msg = msg;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    @Override
    public String toString() {
        return "SendMessageRequest{msg=" + msg + "}";
    }
}
