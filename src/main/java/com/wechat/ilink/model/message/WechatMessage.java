package com.wechat.ilink.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信消息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WechatMessage {
    
    @JsonProperty("from_user_id")
    private String fromUserId;

    @JsonProperty("to_user_id")
    private String toUserId;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("message_type")
    private Integer messageType;

    @JsonProperty("message_state")
    private Integer messageState;

    @JsonProperty("item_list")
    private List<MessageItem> itemList;

    @JsonProperty("context_token")
    private String contextToken;

    @JsonProperty("create_time_ms")
    private Long createTimeMs;

    public WechatMessage() {
        this.itemList = new ArrayList<>();
    }

    /**
     * 创建回复消息
     */
    public static WechatMessage createReply(String toUserId, String content, String contextToken) {
        WechatMessage msg = new WechatMessage();
        msg.setToUserId(toUserId);
        msg.setMessageType(MessageType.BOT);
        msg.setMessageState(MessageType.State.FINISH);
        msg.setContextToken(contextToken);

        List<MessageItem> items = new ArrayList<>();
        items.add(MessageItem.createText(content));
        msg.setItemList(items);

        return msg;
    }

    /**
     * 判断是否为有效的用户消息
     */
    public boolean isUserMessage() {
        return messageType != null && messageType == MessageType.USER;
    }

    /**
     * 提取消息中的文本内容
     */
    public String extractTextContent() {
        if (itemList == null || itemList.isEmpty()) {
            return "";
        }

        for (MessageItem item : itemList) {
            String text = item.extractText();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "";
    }

    /**
     * 获取发送者用户名（去掉@im.wechat后缀）
     */
    public String getSenderName() {
        if (fromUserId == null) {
            return "";
        }
        int atIndex = fromUserId.indexOf('@');
        return atIndex > 0 ? fromUserId.substring(0, atIndex) : fromUserId;
    }

    // Getters and Setters

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageState() {
        return messageState;
    }

    public void setMessageState(Integer messageState) {
        this.messageState = messageState;
    }

    public List<MessageItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<MessageItem> itemList) {
        this.itemList = itemList;
    }

    public String getContextToken() {
        return contextToken;
    }

    public void setContextToken(String contextToken) {
        this.contextToken = contextToken;
    }

    public Long getCreateTimeMs() {
        return createTimeMs;
    }

    public void setCreateTimeMs(Long createTimeMs) {
        this.createTimeMs = createTimeMs;
    }

    @Override
    public String toString() {
        return "WechatMessage{" +
                "fromUserId='" + fromUserId + "'" +
                ", toUserId='" + toUserId + "'" +
                ", messageType=" + messageType +
                ", contextToken='" + (contextToken != null ? "***" : null) + "'" +
                ", content='" + extractTextContent() + "'" +
                "}";
    }
}
