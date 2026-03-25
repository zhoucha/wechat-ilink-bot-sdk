package com.wechat.ilink.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 引用消息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefMessage {

    @JsonProperty("message_item")
    private MessageItem messageItem;

    @JsonProperty("title")
    private String title;

    public RefMessage() {
    }

    public MessageItem getMessageItem() {
        return messageItem;
    }

    public void setMessageItem(MessageItem messageItem) {
        this.messageItem = messageItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "RefMessage{title='" + title + "', messageItem=" + messageItem + "}";
    }
}
