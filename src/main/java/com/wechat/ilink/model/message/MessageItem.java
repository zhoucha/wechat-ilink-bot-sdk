package com.wechat.ilink.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息项（可包含文本、语音、引用等）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageItem {

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("text_item")
    private TextItem textItem;

    @JsonProperty("voice_item")
    private VoiceItem voiceItem;

    @JsonProperty("ref_msg")
    private RefMessage refMsg;

    public MessageItem() {
    }

    public static MessageItem createText(String text) {
        MessageItem item = new MessageItem();
        item.setType(MessageType.Item.TEXT);
        item.setTextItem(new TextItem(text));
        return item;
    }

    public static MessageItem createVoice(String text) {
        MessageItem item = new MessageItem();
        item.setType(MessageType.Item.VOICE);
        item.setVoiceItem(new VoiceItem(text));
        return item;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public TextItem getTextItem() {
        return textItem;
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
    }

    public VoiceItem getVoiceItem() {
        return voiceItem;
    }

    public void setVoiceItem(VoiceItem voiceItem) {
        this.voiceItem = voiceItem;
    }

    public RefMessage getRefMsg() {
        return refMsg;
    }

    public void setRefMsg(RefMessage refMsg) {
        this.refMsg = refMsg;
    }

    /**
     * 提取消息项中的文本内容
     */
    public String extractText() {
        if (type == null) {
            return "";
        }

        switch (type) {
            case MessageType.Item.TEXT:
                if (textItem != null && textItem.getText() != null) {
                    StringBuilder sb = new StringBuilder();
                    // 处理引用消息
                    if (refMsg != null && refMsg.getTitle() != null) {
                        sb.append("[引用: ").append(refMsg.getTitle()).append("]\n");
                    }
                    sb.append(textItem.getText());
                    return sb.toString();
                }
                break;
            case MessageType.Item.VOICE:
                if (voiceItem != null) {
                    return voiceItem.getText();
                }
                break;
            default:
                break;
        }
        return "";
    }

    @Override
    public String toString() {
        return "MessageItem{type=" + type + ", text=" + extractText() + "}";
    }
}
