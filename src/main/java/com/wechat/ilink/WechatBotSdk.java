package com.wechat.ilink;

import com.wechat.ilink.api.IlinkApiClient;
import com.wechat.ilink.auth.QrcodeLoginManager;
import com.wechat.ilink.cache.ContextTokenCache;
import com.wechat.ilink.config.SdkConfig;
import com.wechat.ilink.exception.AuthException;
import com.wechat.ilink.exception.QrcodeTimeoutException;
import com.wechat.ilink.listener.MessageListener;
import com.wechat.ilink.listener.MessageListenerService;
import com.wechat.ilink.model.auth.Credentials;
import com.wechat.ilink.model.message.WechatMessage;
import com.wechat.ilink.model.response.QrcodeResponse;
import com.wechat.ilink.persistence.CredentialsStore;
import com.wechat.ilink.persistence.Store;
import com.wechat.ilink.persistence.SyncBufferStore;
import com.wechat.ilink.sender.MessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 微信 ilink Bot SDK 入口类
 * <p>
 * 使用示例：
 * <pre>
 * WechatBotSdk sdk = new WechatBotSdk("~/.claude/");
 *
 * // 检查是否已登录
 * if (!sdk.isAuthenticated()) {
 *     // 扫码登录
 *     QrcodeResponse qrcode = sdk.getQrcode();
 *     // 显示二维码...
 *     Credentials credentials = sdk.loginWithQrcode(qrcode.getQrcode(), callback);
 * }
 *
 * // 启动消息监听
 * sdk.addMessageListener(new SimpleMessageListener() {
 *     public void onMessagesReceived(List&lt;WechatMessage&gt; messages) {
 *         for (WechatMessage msg : messages) {
 *             if (msg.isUserMessage()) {
 *                 String text = msg.extractTextContent();
 *                 // 处理消息并回复
 *                 sdk.sendTextMessage(msg.getFromUserId(), "收到: " + text);
 *             }
 *         }
 *     }
 * });
 * sdk.startListening();
 * </pre>
 */
public class WechatBotSdk implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(WechatBotSdk.class);

    private final SdkConfig config;
    private final Store credentialsStore;
    private final SyncBufferStore syncBufferStore;
    private final ContextTokenCache contextTokenCache;
    private final IlinkApiClient apiClient;
    private final QrcodeLoginManager loginManager;

    private final MessageListenerService messageListener;

    private final MessageSenderService messageSender;

    /**
     * 创建 SDK 实例
     *
     * @param configDir 配置目录路径（用于存储凭据和同步缓冲区）
     */
    public WechatBotSdk(String configDir) {
        this(configDir, new SdkConfig());
    }

    /**
     * 创建 SDK 实例（自定义配置）
     *
     * @param configDir 配置目录路径
     * @param config    SDK配置
     */
    public WechatBotSdk(String configDir, SdkConfig config) {
        this.config = config;

        // 初始化持久化存储
        this.credentialsStore = new CredentialsStore(configDir);
        this.syncBufferStore = new SyncBufferStore(configDir);
        this.contextTokenCache = new ContextTokenCache();

        // 初始化API客户端
        this.apiClient = new IlinkApiClient(SdkConfig.DEFAULT_BASE_URL);

        // 加载已有凭据
        loadCredentials();

        // 初始化服务
        this.loginManager = new QrcodeLoginManager(apiClient, config);
        this.messageListener = new MessageListenerService(apiClient, config, syncBufferStore, contextTokenCache);
        this.messageSender = new MessageSenderService(apiClient, config, contextTokenCache);
    }

    private void loadCredentials() {
        try {
            Credentials credentials = credentialsStore.load();
            if (credentials != null && credentials.isValid()) {
                apiClient.setCredentials(credentials);
                logger.info("Loaded existing credentials for account: {}", credentials.getAccountId());
            }
        } catch (IOException e) {
            logger.warn("Failed to load credentials: {}", e.getMessage());
        }
    }

    /**
     * 检查是否已认证
     */
    public boolean isAuthenticated() {
        Credentials credentials = apiClient.getCredentials();
        return credentials != null && credentials.isValid();
    }

    /**
     * 获取登录二维码
     */
    public QrcodeResponse getQrcode() throws IOException {
        return loginManager.getQrcode();
    }

    /**
     * 使用二维码登录
     *
     * @param qrcodeToken 二维码标识
     * @param callback    状态回调（可为null）
     * @return 登录凭据
     */
    public Credentials loginWithQrcode(String qrcodeToken, QrcodeLoginManager.QrcodeStatusCallback callback)
            throws IOException, InterruptedException, AuthException, QrcodeTimeoutException {
        Credentials credentials = loginManager.pollQrcodeStatus(qrcodeToken, callback);

        // 保存凭据
        try {
            credentialsStore.save(credentials);
            logger.info("Credentials saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save credentials", e);
        }

        return credentials;
    }

    /**
     * 添加消息监听器
     */
    public void addMessageListener(MessageListener listener) {
        messageListener.addListener(listener);
    }

    /**
     * 移除消息监听器
     */
    public void removeMessageListener(MessageListener listener) {
        messageListener.removeListener(listener);
    }

    /**
     * 启动消息监听
     */
    public void startListening() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Not authenticated. Please login first.");
        }
        messageListener.start();
    }

    /**
     * 停止消息监听
     */
    public void stopListening() {
        messageListener.stop();
    }

    /**
     * 发送文本消息
     *
     * @param toUserId 接收者ID（xxx@im.wechat格式）
     * @param content  消息内容
     * @return 是否发送成功
     */
    public boolean sendTextMessage(String toUserId, String content) throws IOException {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Not authenticated. Please login first.");
        }
        return messageSender.sendTextMessage(toUserId, content);
    }

    /**
     * 发送自定义消息
     */
    public boolean sendMessage(WechatMessage message) throws IOException {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Not authenticated. Please login first.");
        }
        return messageSender.sendMessage(message);
    }

    /**
     * 退出登录并清除凭据
     */
    public void logout() {
        stopListening();
        try {
            credentialsStore.clear();
            syncBufferStore.clear();
            contextTokenCache.clear();
            apiClient.setCredentials(null);
            logger.info("Logged out and cleared credentials");
        } catch (IOException e) {
            logger.error("Failed to clear credentials", e);
        }
    }

    /**
     * 获取配置
     */
    public SdkConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        stopListening();
        try {
            apiClient.close();
        } catch (IOException e) {
            logger.error("Error closing API client", e);
        }
    }
}
