package com.wechat.ilink.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageType 单元测试
 */
class MessageTypeTest {

    @Test
    void testUser消息类型为1() {
        assertEquals(1, MessageType.USER);
    }

    @Test
    void testBot消息类型为2() {
        assertEquals(2, MessageType.BOT);
    }

    @Test
    void testItem文本类型为1() {
        assertEquals(1, MessageType.Item.TEXT);
    }

    @Test
    void testItem语音类型为3() {
        assertEquals(3, MessageType.Item.VOICE);
    }

    @Test
    void testState完成状态为2() {
        assertEquals(2, MessageType.State.FINISH);
    }

    @Test
    void test工具类私有构造函数不能被实例化() {
        // 由于构造函数是私有的，检查构造函数是否存在且为private
        var constructors = MessageType.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    void testItem内部类私有构造函数不能被实例化() {
        var constructors = MessageType.Item.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    void testState内部类私有构造函数不能被实例化() {
        var constructors = MessageType.State.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }
}