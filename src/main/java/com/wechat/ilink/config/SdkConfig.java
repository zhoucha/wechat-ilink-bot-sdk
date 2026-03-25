package com.wechat.ilink.config;

/**
 * SDK 配置
 */
public class SdkConfig {

    public static final String DEFAULT_BASE_URL = "https://ilinkai.weixin.qq.com";
    public static final String CHANNEL_VERSION = "0.1.0";
    public static final String BOT_TYPE = "3";

    // 长轮询超时：35秒
    public static final long LONG_POLLING_TIMEOUT_MS = 35000;

    // 发送消息超时：15秒
    public static final long SEND_TIMEOUT_MS = 15000;

    // 扫码超时：8分钟
    public static final long QR_CODE_TIMEOUT_MS = 8 * 60 * 1000;

    // 扫码轮询间隔：1秒
    public static final long QR_CODE_POLL_INTERVAL_MS = 1000;

    // 最大连续失败次数
    public static final int MAX_CONSECUTIVE_FAILURES = 3;

    // 重试延迟：2秒
    public static final long RETRY_DELAY_MS = 2000;

    // 退避延迟：30秒
    public static final long BACKOFF_DELAY_MS = 30000;

    private long longPollingTimeoutMs = LONG_POLLING_TIMEOUT_MS;
    private long sendTimeoutMs = SEND_TIMEOUT_MS;
    private long qrcodeTimeoutMs = QR_CODE_TIMEOUT_MS;
    private long qrcodePollIntervalMs = QR_CODE_POLL_INTERVAL_MS;
    private int maxConsecutiveFailures = MAX_CONSECUTIVE_FAILURES;
    private long retryDelayMs = RETRY_DELAY_MS;
    private long backoffDelayMs = BACKOFF_DELAY_MS;

    // Getters and Setters (保留默认值)

    public long getLongPollingTimeoutMs() {
        return longPollingTimeoutMs;
    }

    public void setLongPollingTimeoutMs(long longPollingTimeoutMs) {
        this.longPollingTimeoutMs = longPollingTimeoutMs;
    }

    public long getSendTimeoutMs() {
        return sendTimeoutMs;
    }

    public void setSendTimeoutMs(long sendTimeoutMs) {
        this.sendTimeoutMs = sendTimeoutMs;
    }

    public long getQrcodeTimeoutMs() {
        return qrcodeTimeoutMs;
    }

    public void setQrcodeTimeoutMs(long qrcodeTimeoutMs) {
        this.qrcodeTimeoutMs = qrcodeTimeoutMs;
    }

    public long getQrcodePollIntervalMs() {
        return qrcodePollIntervalMs;
    }

    public void setQrcodePollIntervalMs(long qrcodePollIntervalMs) {
        this.qrcodePollIntervalMs = qrcodePollIntervalMs;
    }

    public int getMaxConsecutiveFailures() {
        return maxConsecutiveFailures;
    }

    public void setMaxConsecutiveFailures(int maxConsecutiveFailures) {
        this.maxConsecutiveFailures = maxConsecutiveFailures;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    public long getBackoffDelayMs() {
        return backoffDelayMs;
    }

    public void setBackoffDelayMs(long backoffDelayMs) {
        this.backoffDelayMs = backoffDelayMs;
    }
}
