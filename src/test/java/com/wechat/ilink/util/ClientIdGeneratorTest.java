package com.wechat.ilink.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientIdGenerator 单元测试
 */
class ClientIdGeneratorTest {

    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("client:\\d+-[0-9a-f]{8}");

    @Test
    void testGenerate返回非空字符串() {
        String clientId = ClientIdGenerator.generate();
        assertNotNull(clientId);
        assertFalse(clientId.isEmpty());
    }

    @Test
    void testGenerate返回正确格式() {
        String clientId = ClientIdGenerator.generate();
        assertTrue(CLIENT_ID_PATTERN.matcher(clientId).matches(),
                "ClientId格式应为 client:{timestamp}-{4字节随机hex}, 实际: " + clientId);
    }

    @Test
    void testGenerate生成不同值() {
        Set<String> clientIds = new HashSet<>();
        // 生成100个不同的clientId
        for (int i = 0; i < 100; i++) {
            clientIds.add(ClientIdGenerator.generate());
        }
        // 由于包含时间戳，几乎肯定会生成不同的值
        assertTrue(clientIds.size() > 90, "应该生成大部分不同的值");
    }


}