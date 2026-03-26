package com.wechat.ilink.sender;

import com.wechat.ilink.api.IlinkApiClient;
import com.wechat.ilink.cache.ContextTokenCache;
import com.wechat.ilink.config.SdkConfig;
import com.wechat.ilink.exception.ApiException;
import com.wechat.ilink.model.message.WechatMessage;
import com.wechat.ilink.model.request.SendMessageRequest;
import com.wechat.ilink.model.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 消息发送服务
 */
public class MessageSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);

    private final IlinkApiClient apiClient;
    private final SdkConfig config;
    private final ContextTokenCache contextTokenCache;

    public MessageSenderService(IlinkApiClient apiClient, SdkConfig config, ContextTokenCache contextTokenCache) {
        this.apiClient = apiClient;
        this.config = config;
        this.contextTokenCache = contextTokenCache;
    }

    /**
     * 发送文本消息
     *
     * @param toUserId 接收者ID（xxx@im.wechat格式）
     * @param content  消息内容
     * @return 发送结果
     */
    public boolean sendTextMessage(String toUserId, String content) throws ApiException {
        String contextToken = contextTokenCache.getOrThrow(toUserId);
        WechatMessage msg = WechatMessage.createReply(toUserId, content, contextToken);
        SendMessageRequest request = new SendMessageRequest(msg);

        ApiResponse response = apiClient.sendMessage(request, Duration.ofMillis(config.getSendTimeoutMs()));

        if (response.isSuccess()) {
            logger.debug("Message sent to {} successfully", toUserId);
            return true;
        } else {
            logger.warn("Failed to send message to {}: {}", toUserId, response.getErrorInfo());
            return false;
        }
    }

    /**
     * 发送消息（使用自定义消息对象）
     */
    public boolean sendMessage(WechatMessage message) throws ApiException {
        if (message.getContextToken() == null) {
            message.setContextToken(contextTokenCache.getOrThrow(message.getToUserId()));
        }
        SendMessageRequest request = new SendMessageRequest(message);
        ApiResponse response = apiClient.sendMessage(request, Duration.ofMillis(config.getSendTimeoutMs()));
        if (response.isSuccess()) {
            logger.debug("Message sent to {} successfully", message.getToUserId());
            return true;
        } else {
            logger.warn("Failed to send message: {}", response.getErrorInfo());
            return false;
        }
    }
}
