package com.wechat.ilink.model.message;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WechatMessage 单元测试
 */
class WechatMessageTest {

    @Test
    void testConstructorInitializesEmptyItemList() {
        WechatMessage message = new WechatMessage();
        assertNotNull(message.getItemList());
        assertTrue(message.getItemList().isEmpty());
    }

    @Test
    void testCreateReply() {
        WechatMessage reply = WechatMessage.createReply("toUser", "Hello", "token123");

        assertEquals("toUser", reply.getToUserId());
        assertEquals(MessageType.BOT, reply.getMessageType());
        assertEquals(MessageType.State.FINISH, reply.getMessageState());
        assertEquals("token123", reply.getContextToken());
        assertNotNull(reply.getItemList());
        assertEquals(1, reply.getItemList().size());
    }

    @Test
    void testIsUserMessageReturnsTrueWhenTypeIsUser() {
        WechatMessage message = new WechatMessage();
        message.setMessageType(MessageType.USER);

        assertTrue(message.isUserMessage());
    }

    @Test
    void testIsUserMessageReturnsFalseWhenTypeIsBot() {
        WechatMessage message = new WechatMessage();
        message.setMessageType(MessageType.BOT);

        assertFalse(message.isUserMessage());
    }

    @Test
    void testIsUserMessageReturnsFalseWhenTypeIsNull() {
        WechatMessage message = new WechatMessage();

        assertFalse(message.isUserMessage());
    }

    @Test
    void testExtractTextContentReturnsTextContent() {
        WechatMessage message = new WechatMessage();
        List<MessageItem> items = new ArrayList<>();
        items.add(MessageItem.createText("Hello World"));
        message.setItemList(items);

        assertEquals("Hello World", message.extractTextContent());
    }

    @Test
    void testExtractTextContentReturnsEmptyStringWhenItemListEmpty() {
        WechatMessage message = new WechatMessage();
        message.setItemList(new ArrayList<>());

        assertEquals("", message.extractTextContent());
    }

    @Test
    void testExtractTextContentReturnsEmptyStringWhenItemListNull() {
        WechatMessage message = new WechatMessage();
        message.setItemList(null);

        assertEquals("", message.extractTextContent());
    }

    @Test
    void testExtractTextContentReturnsFirstNonEmptyText() {
        WechatMessage message = new WechatMessage();
        List<MessageItem> items = new ArrayList<>();
        items.add(MessageItem.createText("First text"));
        items.add(MessageItem.createText("Second text"));
        message.setItemList(items);

        assertEquals("First text", message.extractTextContent());
    }

    @Test
    void testGetSenderNameReturnsUsernameWhenContainsAtSymbol() {
        WechatMessage message = new WechatMessage();
        message.setFromUserId("user123@im.wechat");

        assertEquals("user123", message.getSenderName());
    }

    @Test
    void testGetSenderNameReturnsOriginalValueWhenNoAtSymbol() {
        WechatMessage message = new WechatMessage();
        message.setFromUserId("user123");

        assertEquals("user123", message.getSenderName());
    }

    @Test
    void testGetSenderNameReturnsEmptyStringWhenFromUserIdNull() {
        WechatMessage message = new WechatMessage();

        assertEquals("", message.getSenderName());
    }

    @Test
    void testGetterSetter() {
        WechatMessage message = new WechatMessage();

        message.setFromUserId("fromUser");
        message.setToUserId("toUser");
        message.setClientId("client1");
        message.setSessionId("session1");
        message.setMessageType(MessageType.USER);
        message.setMessageState(MessageType.State.FINISH);
        message.setContextToken("token123");
        message.setCreateTimeMs(12345678L);

        assertEquals("fromUser", message.getFromUserId());
        assertEquals("toUser", message.getToUserId());
        assertEquals("client1", message.getClientId());
        assertEquals("session1", message.getSessionId());
        assertEquals(MessageType.USER, message.getMessageType());
        assertEquals(MessageType.State.FINISH, message.getMessageState());
        assertEquals("token123", message.getContextToken());
        assertEquals(12345678L, message.getCreateTimeMs());
    }
}