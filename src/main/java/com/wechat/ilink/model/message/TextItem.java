package com.wechat.ilink.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 文本消息项
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextItem {

    @JsonProperty("text")
    private String text;

    public TextItem() {
    }

    public TextItem(String text) {
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
        return "TextItem{text='" + text + "'}";
    }
}
