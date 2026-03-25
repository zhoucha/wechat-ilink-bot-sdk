## SDK 项目结构

```
wechat-ilink-bot-sdk/
├── pom.xml                                # Maven 构建配置
├── README.md                              # 项目说明文档
└── src/
    └── main/
        └── java/
            └── com/
                └── wechat/
                    └── ilink/
                        ├── WechatBotSdk.java           # SDK 入口类
                        ├── api/
                        │   └── IlinkApiClient.java     # HTTP API 客户端
                        ├── auth/
                        │   └── QrcodeLoginManager.java # 扫码登录管理
                        ├── cache/
                        │   └── ContextTokenCache.java  # Context Token 缓存
                        ├── config/
                        │   └── SdkConfig.java          # SDK 配置
                        ├── exception/
                        │   ├── ApiException.java       # API 异常
                        │   ├── AuthException.java      # 认证异常
                        │   ├── QrcodeTimeoutException.java  # 扫码超时异常
                        │   └── WechatIlinkException.java # 异常基类
                        ├── http/
                        │   └── HttpClientWrapper.java  # HTTP 客户端封装
                        ├── listener/
                        │   ├── MessageListener.java    # 消息监听器接口
                        │   ├── MessageListenerService.java  # 消息监听服务
                        │   └── SimpleMessageListener.java   # 适配器类
                        ├── model/
                        │   ├── auth/
                        │   │   └── Credentials.java  # 凭据对象
                        │   ├── message/
                        │   │   ├── MessageItem.java  # 消息项
                        │   │   ├── MessageType.java  # 消息类型常量
                        │   │   ├── RefMessage.java   # 引用消息
                        │   │   ├── TextItem.java     # 文本项
                        │   │   ├── VoiceItem.java    # 语音项
                        │   │   └── WechatMessage.java# 微信消息对象
                        │   ├── request/
                        │   │   ├── BaseInfo.java           # 基础信息
                        │   │   ├── GetUpdatesRequest.java  # 获取消息请求
                        │   │   └── SendMessageRequest.java # 发送消息请求
                        │   └── response/
                        │       ├── ApiResponse.java          # API 响应基类
                        │       ├── GetUpdatesResponse.java   # 消息响应
                        │       ├── QrcodeResponse.java       # 二维码响应
                        │       ├── QrcodeStatus.java         # 二维码状态枚举
                        │       └── QrcodeStatusResponse.java # 状态响应
                        ├── persistence/
                        │   ├── CredentialsStore.java # 凭据存储
                        │   └── SyncBufferStore.java  # 同步缓冲区存储
                        ├── sender/
                        │   └── MessageSenderService.java  # 消息发送服务
                        ├── util/
                        │   ├── ClientIdGenerator.java  # 客户端ID生成
                        │   └── UinGenerator.java       # UIN 生成
                        └── example/
                            └── BotExample.java       # 使用示例
```

## 设计原则

### 1. 面向对象设计
- `WechatMessage` 封装消息相关业务逻辑
- `ApiResponse` 提供统一的响应处理
- 使用 Builder 模式创建复杂对象

### 2. 高内聚
- `QrcodeLoginManager` 专责扫码登录
- `MessageListenerService` 专责消息监听
- `MessageSenderService` 专责消息发送
- 每个服务类职责单一

### 3. 低耦合
- 通过接口 `MessageListener` 解耦消息处理
- `HttpClientWrapper` 隔离 HTTP 实现细节
- 配置对象 `SdkConfig` 支持参数定制

### 4. 可扩展性
- `MessageListener` 接口支持自定义监听器
- `SdkConfig` 支持灵活配置
- 异常体系支持精细化错误处理
