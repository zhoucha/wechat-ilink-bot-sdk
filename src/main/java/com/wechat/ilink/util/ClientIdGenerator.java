package com.wechat.ilink.util;

import java.security.SecureRandom;

/**
 * Client ID 生成工具
 */
public final class ClientIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private ClientIdGenerator() {
        // 私有构造函数，防止实例化
    }

    /**
     * 生成客户端ID
     * 格式：client:{timestamp}-{4字节随机hex}
     */
    public static String generate() {
        byte[] randomBytes = new byte[4];
        RANDOM.nextBytes(randomBytes);
        StringBuilder hex = new StringBuilder();
        for (byte b : randomBytes) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return "client:" + System.currentTimeMillis() + "-" + hex.toString();
    }
}
