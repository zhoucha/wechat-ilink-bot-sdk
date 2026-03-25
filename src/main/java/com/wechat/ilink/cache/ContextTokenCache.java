package com.wechat.ilink.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context Token 缓存
 */
public class ContextTokenCache {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * 缓存用户的 context token
     */
    public void put(String userId, String contextToken) {
        if (userId != null && contextToken != null) {
            cache.put(userId, contextToken);
        }
    }

    /**
     * 获取用户的 context token
     */
    public String get(String userId) {
        return cache.get(userId);
    }

    /**
     * 获取用户的 context token，如果不存在则抛出异常
     */
    public String getOrThrow(String userId) {
        String token = cache.get(userId);
        if (token == null) {
            throw new IllegalStateException("No context token for user: " + userId);
        }
        return token;
    }

    /**
     * 删除用户的 context token
     */
    public void remove(String userId) {
        cache.remove(userId);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
    }

    /**
     * 获取缓存大小
     */
    public int size() {
        return cache.size();
    }
}
