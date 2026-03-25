package com.wechat.ilink.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 语音消息项（已转文字）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoiceItem {

    @JsonProperty("text")
    private String text;

    public VoiceItem() {
    }

    public VoiceItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "VoiceItem{text='" + text + "'}";
    }
}
