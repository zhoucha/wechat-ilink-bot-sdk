package com.wechat.ilink.config;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SdkConfig 单元测试
 */
class SdkConfigTest {

    @Test
    void test默认构造函数设置默认值() {
        SdkConfig config = new SdkConfig();

        assertEquals(SdkConfig.LONG_POLLING_TIMEOUT_MS, config.getLongPollingTimeoutMs());
        assertEquals(SdkConfig.SEND_TIMEOUT_MS, config.getSendTimeoutMs());
        assertEquals(SdkConfig.QR_CODE_TIMEOUT_MS, config.getQrcodeTimeoutMs());
        assertEquals(SdkConfig.QR_CODE_POLL_INTERVAL_MS, config.getQrcodePollIntervalMs());
        assertEquals(SdkConfig.MAX_CONSECUTIVE_FAILURES, config.getMaxConsecutiveFailures());
        assertEquals(SdkConfig.RETRY_DELAY_MS, config.getRetryDelayMs());
        assertEquals(SdkConfig.BACKOFF_DELAY_MS, config.getBackoffDelayMs());
    }

    @Test
    void test设置长轮询超时() {
        SdkConfig config = new SdkConfig();
        long customTimeout = 60000L;
        config.setLongPollingTimeoutMs(customTimeout);

        assertEquals(customTimeout, config.getLongPollingTimeoutMs());
    }

    @Test
    void test设置发送消息超时() {
        SdkConfig config = new SdkConfig();
        long customTimeout = 20000L;
        config.setSendTimeoutMs(customTimeout);

        assertEquals(customTimeout, config.getSendTimeoutMs());
    }

    @Test
    void test设置扫码超时() {
        SdkConfig config = new SdkConfig();
        long customTimeout = 10 * 60 * 1000L;
        config.setQrcodeTimeoutMs(customTimeout);

        assertEquals(customTimeout, config.getQrcodeTimeoutMs());
    }

    @Test
    void test设置扫码轮询间隔() {
        SdkConfig config = new SdkConfig();
        long customInterval = 500L;
        config.setQrcodePollIntervalMs(customInterval);

        assertEquals(customInterval, config.getQrcodePollIntervalMs());
    }

    @Test
    void test设置最大连续失败次数() {
        SdkConfig config = new SdkConfig();
        int maxFailures = 5;
        config.setMaxConsecutiveFailures(maxFailures);

        assertEquals(maxFailures, config.getMaxConsecutiveFailures());
    }

    @Test
    void test设置重试延迟() {
        SdkConfig config = new SdkConfig();
        long customDelay = 5000L;
        config.setRetryDelayMs(customDelay);

        assertEquals(customDelay, config.getRetryDelayMs());
    }

    @Test
    void test设置退避延迟() {
        SdkConfig config = new SdkConfig();
        long customDelay = 60000L;
        config.setBackoffDelayMs(customDelay);

        assertEquals(customDelay, config.getBackoffDelayMs());
    }

    @Test
    void test常量定义正确() {
        assertEquals("https://ilinkai.weixin.qq.com", SdkConfig.DEFAULT_BASE_URL);
        assertEquals("0.1.0", SdkConfig.CHANNEL_VERSION);
        assertEquals("3", SdkConfig.BOT_TYPE);
        assertEquals(35000, SdkConfig.LONG_POLLING_TIMEOUT_MS);
        assertEquals(15000, SdkConfig.SEND_TIMEOUT_MS);
        assertEquals(480000, SdkConfig.QR_CODE_TIMEOUT_MS); // 8分钟
        assertEquals(1000, SdkConfig.QR_CODE_POLL_INTERVAL_MS);
        assertEquals(3, SdkConfig.MAX_CONSECUTIVE_FAILURES);
        assertEquals(2000, SdkConfig.RETRY_DELAY_MS);
        assertEquals(30000, SdkConfig.BACKOFF_DELAY_MS);
    }
}