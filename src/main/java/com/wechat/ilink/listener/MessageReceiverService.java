package com.wechat.ilink.listener;

import com.wechat.ilink.api.IlinkApiClient;
import com.wechat.ilink.cache.ContextTokenCache;
import com.wechat.ilink.config.SdkConfig;
import com.wechat.ilink.model.message.WechatMessage;
import com.wechat.ilink.model.request.GetUpdatesRequest;
import com.wechat.ilink.model.response.GetUpdatesResponse;
import com.wechat.ilink.persistence.SyncBufferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息监听服务
 */
public class MessageReceiverService implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

    private final IlinkApiClient apiClient;
    private final SdkConfig config;
    private final SyncBufferStore syncBufferStore;
    private final ContextTokenCache contextTokenCache;
    private final List<MessageListener> listeners = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread listenerThread;
    private int consecutiveFailures = 0;

    public MessageReceiverService(IlinkApiClient apiClient, SdkConfig config, SyncBufferStore syncBufferStore, ContextTokenCache contextTokenCache) {
        this.apiClient = apiClient;
        this.config = config;
        this.syncBufferStore = syncBufferStore;
        this.contextTokenCache = contextTokenCache;
    }

    /**
     * 添加消息监听器
     */
    public void addListener(MessageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * 移除消息监听器
     */
    public void removeListener(MessageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * 启动消息监听
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            listenerThread = Thread.ofVirtual()
                    .name("message-listener")
                    .start(this::runLoop);
            logger.info("消息监听服务启动成功!");
        }
    }

    /**
     * 停止消息监听
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (listenerThread != null) {
                listenerThread.interrupt();
                listenerThread = null;
            }
            logger.info("消息监听启动停止成功!");
        }
    }

    private void runLoop() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                String currentBuffer = syncBufferStore.getBuffer();
                GetUpdatesRequest request = new GetUpdatesRequest(currentBuffer, SdkConfig.CHANNEL_VERSION);

                GetUpdatesResponse response = apiClient.getUpdates(
                        request,
                        Duration.ofMillis(config.getLongPollingTimeoutMs())
                );

                if (response.isSuccess()) {
                    consecutiveFailures = 0;

                    // 保存新的同步缓冲区
                    if (response.getGetUpdatesBuf() != null) {
                        syncBufferStore.updateBuffer(response.getGetUpdatesBuf());
                    }

                    // 处理消息
                    if (response.getMsgs() != null && !response.getMsgs().isEmpty()) {
                        List<WechatMessage> userMessages = new ArrayList<>();

                        for (WechatMessage msg : response.getMsgs()) {
                            // 只处理用户消息
                            if (msg.isUserMessage()) {
                                // 缓存 context token
                                if (msg.getContextToken() != null && msg.getFromUserId() != null) {
                                    contextTokenCache.put(msg.getFromUserId(), msg.getContextToken());
                                }
                                userMessages.add(msg);
                            }
                        }
                        if (!userMessages.isEmpty()) {
                            notifyMessagesReceived(userMessages);
                        }
                    }
                } else {
                    handleFailure(new Exception("Api 错误: " + response.getErrorInfo()));
                }

            } catch (Exception e) {
                if (isTimeoutException(e)) {
                    // 长轮询超时是正常的
                    notifyPollingTimeout();
                } else {
                    handleFailure(e);
                }
            }
        }
    }

    private void handleFailure(Exception e) {
        consecutiveFailures++;
        logger.warn("消息轮询失败 #{}: {}", consecutiveFailures, e.getMessage());
        notifyError(e);
        if (consecutiveFailures >= config.getMaxConsecutiveFailures()) {
            // 长时间退避
            logger.info("消息轮询退避 {}ms", config.getBackoffDelayMs());
            notifyDisconnected();
            sleep(config.getBackoffDelayMs());
            consecutiveFailures = 0;
            notifyReconnected();
        } else {
            // 短时间重试
            sleep(config.getRetryDelayMs());
        }
    }

    private boolean isTimeoutException(Exception e) {
        String message = e.getMessage();
        return message != null && (message.contains("Read timed out") ||
                message.contains("SocketTimeoutException") ||
                message.contains("HttpTimeoutException"));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 通知方法

    private void notifyMessagesReceived(List<WechatMessage> messages) {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                try {
                    listener.onMessagesReceived(messages);
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            }
        }
    }

    private void notifyPollingTimeout() {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                try {
                    listener.onPollingTimeout();
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            }
        }
    }

    private void notifyError(Exception error) {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                try {
                    listener.onError(error);
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            }
        }
    }

    private void notifyDisconnected() {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                try {
                    listener.onDisconnected();
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            }
        }
    }

    private void notifyReconnected() {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                try {
                    listener.onReconnected();
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            }
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void close() {
        stop();
    }
}
