package com.wechat.ilink.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageItem 单元测试
 */
class MessageItemTest {

    @Test
    void testCreateText() {
        MessageItem item = MessageItem.createText("Hello World");

        assertEquals(MessageType.Item.TEXT, item.getType());
        assertNotNull(item.getTextItem());
        assertEquals("Hello World", item.getTextItem().getText());
    }

    @Test
    void testCreateVoice() {
        MessageItem item = MessageItem.createVoice("Voice content");

        assertEquals(MessageType.Item.VOICE, item.getType());
        assertNotNull(item.getVoiceItem());
        assertEquals("Voice content", item.getVoiceItem().getText());
    }

    @Test
    void testExtractTextWithTextItem() {
        MessageItem item = MessageItem.createText("Test text");
        assertEquals("Test text", item.extractText());
    }

    @Test
    void testExtractTextWithVoiceItem() {
        MessageItem item = MessageItem.createVoice("Voice text");
        assertEquals("Voice text", item.extractText());
    }

    @Test
    void testExtractTextWithNullType() {
        MessageItem item = new MessageItem();
        assertEquals("", item.extractText());
    }

    @Test
    void testExtractTextWithNullTextItem() {
        MessageItem item = new MessageItem();
        item.setType(MessageType.Item.TEXT);
        assertEquals("", item.extractText());
    }

    @Test
    void testExtractTextWithUnknownType() {
        MessageItem item = new MessageItem();
        item.setType(999);
        assertEquals("", item.extractText());
    }

    @Test
    void testExtractTextIncludesRefMsgTitle() {
        MessageItem item = MessageItem.createText("Main text");
        RefMessage refMsg = new RefMessage();
        refMsg.setTitle("Referenced Title");
        item.setRefMsg(refMsg);

        String result = item.extractText();
        assertTrue(result.contains("Referenced Title"));
        assertTrue(result.contains("Main text"));
    }

    @Test
    void testExtractTextWithRefMsgButNullTitle() {
        MessageItem item = MessageItem.createText("Main text");
        RefMessage refMsg = new RefMessage();
        item.setRefMsg(refMsg);

        assertEquals("Main text", item.extractText());
    }
}