package com.wechat.ilink.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageType 单元测试
 */
class MessageTypeTest {

    @Test
    void testUserMessageTypeEquals1() {
        assertEquals(1, MessageType.USER);
    }

    @Test
    void testBotMessageTypeEquals2() {
        assertEquals(2, MessageType.BOT);
    }

    @Test
    void testItemTextTypeEquals1() {
        assertEquals(1, MessageType.Item.TEXT);
    }

    @Test
    void testItemVoiceTypeEquals3() {
        assertEquals(3, MessageType.Item.VOICE);
    }

    @Test
    void testStateFinishStatusEquals2() {
        assertEquals(2, MessageType.State.FINISH);
    }

    @Test
    void testUtilityClassPrivateConstructorCannotBeInstantiated() {
        // 由于构造函数是私有的，检查构造函数是否存在且为private
        var constructors = MessageType.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    void testItemInnerClassPrivateConstructorCannotBeInstantiated() {
        var constructors = MessageType.Item.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    void testStateInnerClassPrivateConstructorCannotBeInstantiated() {
        var constructors = MessageType.State.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));
    }
}