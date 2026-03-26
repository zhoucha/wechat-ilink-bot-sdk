package com.wechat.ilink.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UinGenerator 单元测试
 */
class UinGeneratorTest {

    @Test
    void testGenerateReturnsNonEmptyString() {
        String uin = UinGenerator.generate();
        assertNotNull(uin);
        assertFalse(uin.isEmpty());
    }

    @Test
    void testGenerateReturnsBase64Encoded() {
        String uin = UinGenerator.generate();
        // Base64解码应该成功
        assertDoesNotThrow(() -> Base64.getDecoder().decode(uin));
    }

    @Test
    void testGenerateReturnsDifferentValues() {
        Set<String> uins = new HashSet<>();
        // 生成100个不同的uin
        for (int i = 0; i < 100; i++) {
            uins.add(UinGenerator.generate());
        }
        // 由于使用SecureRandom，几乎肯定会生成不同的值
        assertTrue(uins.size() > 90, "应该生成大部分不同的值");
    }


}