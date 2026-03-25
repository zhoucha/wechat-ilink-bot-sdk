package com.wechat.ilink.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * UIN 生成工具（用于X-WECHAT-UIN请求头）
 */
public final class UinGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UinGenerator() {
        // 私有构造函数，防止实例化
    }

    /**
     * 生成随机UIN
     * 算法：生成随机4字节uint32，转为字符串后base64编码
     */
    public static String generate() {
        byte[] bytes = new byte[4];
        RANDOM.nextBytes(bytes);
        // 转为无符号32位整数
        long uint32 = ((bytes[0] & 0xFFL) << 24) |
                      ((bytes[1] & 0xFFL) << 16) |
                      ((bytes[2] & 0xFFL) << 8) |
                      (bytes[3] & 0xFFL);
        String uint32Str = String.valueOf(uint32);
        return Base64.getEncoder().encodeToString(uint32Str.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
