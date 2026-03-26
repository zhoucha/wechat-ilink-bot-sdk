# Wechat iLink Bot SDK for Java

微信 iLink Bot SDK，支持扫码登录、消息收发、断点续传等功能。

## 特性

- 高内聚低耦合的模块化设计
- 面向对象的消息模型
- 完整的长轮询消息监听机制
- 自动重试与退避策略
- 断点续传（同步缓冲区持久化）
- Context Token 自动缓存
- 凭据安全存储（文件权限600）

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.wechat</groupId>
    <artifactId>wechat-ilink-bot-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 基本使用

```java
import com.wechat.ilink.WechatBotSdk;
import com.wechat.ilink.listener.SimpleMessageListener;
import com.wechat.ilink.model.message.WechatMessage;

// 创建 SDK 实例
WechatBotSdk sdk = new WechatBotSdk("~/.claude/channels/wechat/");

// 扫码登录流程
if (!sdk.isAuthenticated()) {
    // 获取二维码并显示
    QrcodeResponse qrcode = sdk.getQrcode();
    System.out.println(qrcode.getQrcodeImgContent());

    // 等待用户扫码
    sdk.loginWithQrcode(qrcode.getQrcode(), new QrcodeLoginManager.QrcodeStatusCallback() {
        public void onStatusChanged(QrcodeStatus status, QrcodeStatusResponse response) {
            System.out.println("Status: " + status.getDescription());
        }
        public void onScanned() { System.out.println("Code scanned!"); }
        public void onSuccess(Credentials credentials) { System.out.println("Logged in!"); }
        public void onExpired() { System.out.println("Code expired!"); }
    });
}

// 添加消息监听器
sdk.addMessageListener(new SimpleMessageListener() {
    @Override
    public void onMessagesReceived(List<WechatMessage> messages) {
        for (WechatMessage msg : messages) {
            if (msg.isUserMessage()) {
                String text = msg.extractTextContent();
                System.out.println(msg.getSenderName() + ": " + text);

                // 自动回复
                sdk.sendTextMessage(msg.getFromUserId(), "收到: " + text);
            }
        }
    }
});

// 启动消息监听
sdk.startListening();

// 运行中...
Thread.sleep(Long.MAX_VALUE);
```

## 核心类说明

| 类名 | 说明 |
|------|------|
| `WechatBotSdk` | SDK入口类，提供扫码登录、消息收发等功能 |
| `WechatMessage` | 消息对象模型，支持文本/语音/引用消息 |
| `MessageListener` | 消息监听器接口 |
| `QrcodeLoginManager` | 扫码登录管理器 |
| `MessageListenerService` | 消息监听服务（长轮询） |
| `MessageSenderService` | 消息发送服务 |
| `ContextTokenCache` | Context Token 缓存 |
| `CredentialsStore` | 凭据持久化存储 |
| `SyncBufferStore` | 同步缓冲区持久化 |

## 配置参数

```java
SdkConfig config = new SdkConfig();
config.setLongPollingTimeoutMs(35000);   // 长轮询超时: 35秒
config.setSendTimeoutMs(15000);         // 发送超时: 15秒
config.setMaxConsecutiveFailures(3);    // 最大连续失败次数
config.setRetryDelayMs(2000);           // 重试延迟: 2秒
config.setBackoffDelayMs(30000);        // 退避延迟: 30秒

WechatBotSdk sdk = new WechatBotSdk("~/.claude/", config);
```

## 协议版本

- 协议版本: `0.1.0`
- Bot类型: `3` (ClawBot)
