package com.wechat.ilink.cache;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContextTokenCache 单元测试
 */
class ContextTokenCacheTest {

    @Test
    void testPutAndGet() {
        ContextTokenCache cache = new ContextTokenCache();

        cache.put("user123", "token456");

        assertEquals("token456", cache.get("user123"));
    }

    @Test
    void testGet不存在的Key返回Null() {
        ContextTokenCache cache = new ContextTokenCache();
        assertNull(cache.get("nonexistent"));
    }

    @Test
    void testGetOrThrow存在返回Token() {
        ContextTokenCache cache = new ContextTokenCache();
        cache.put("user123", "token456");

        String token = cache.getOrThrow("user123");

        assertEquals("token456", token);
    }

    @Test
    void testGetOrThrow不存在抛出异常() {
        ContextTokenCache cache = new ContextTokenCache();

        assertThrows(IllegalStateException.class, () -> {
            cache.getOrThrow("nonexistent");
        });
    }

    @Test
    void testRemove() {
        ContextTokenCache cache = new ContextTokenCache();
        cache.put("user123", "token456");

        cache.remove("user123");

        assertNull(cache.get("user123"));
    }

    @Test
    void testClear() {
        ContextTokenCache cache = new ContextTokenCache();
        cache.put("user1", "token1");
        cache.put("user2", "token2");

        cache.clear();

        assertEquals(0, cache.size());
    }

    @Test
    void testSize() {
        ContextTokenCache cache = new ContextTokenCache();
        assertEquals(0, cache.size());

        cache.put("user1", "token1");
        assertEquals(1, cache.size());

        cache.put("user2", "token2");
        assertEquals(2, cache.size());
    }

    @Test
    void testPutNullUserId不抛异常() {
        ContextTokenCache cache = new ContextTokenCache();

        assertDoesNotThrow(() -> cache.put(null, "token"));
        assertEquals(0, cache.size());
    }

    @Test
    void testPutNullToken不抛异常() {
        ContextTokenCache cache = new ContextTokenCache();

        assertDoesNotThrow(() -> cache.put("user", null));
        assertEquals(0, cache.size());
    }

    @Test
    void testConcurrentPutAndGet() throws InterruptedException {
        ContextTokenCache cache = new ContextTokenCache();
        Thread[] threads = new Thread[10];
        final int iterations = 100;

        for (int i = 0; i < 10; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    String userId = "user" + threadNum + "-" + j;
                    String token = "token" + j;
                    cache.put(userId, token);
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(10 * iterations, cache.size());
    }
}