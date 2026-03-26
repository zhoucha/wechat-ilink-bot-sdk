# 微信 ilink API 协议文档

> 微信官方 ClawBot ilink API 完整协议说明  
> 与 `@tencent-weixin/openclaw-weixin` 使用相同协议

---

## 📋 目录

- [基础信息](#基础信息)
- [认证机制](#认证机制)
- [消息接收](#消息接收)
- [消息发送](#消息发送)
- [数据结构](#数据结构)
- [错误处理](#错误处理)
- [最佳实践](#最佳实践)

---

## 基础信息

### API 端点

```
基础 URL: https://ilinkai.weixin.qq.com
协议版本: channel_version: 0.1.0
Bot 类型: 3 (ClawBot)
```

### 通用请求头

```http
Content-Type: application/json
AuthorizationType: ilink_bot_token
X-WECHAT-UIN: {base64编码的随机UIN}
Authorization: Bearer {token}  (登录后必需)
```

#### X-WECHAT-UIN 生成算法

```typescript
// 生成随机 4 字节 uint32
const uint32 = crypto.randomBytes(4).readUInt32BE(0);
// 转为字符串后 base64 编码
return Buffer.from(String(uint32), "utf-8").toString("base64");
```

---

## 认证机制

### 1. 获取登录二维码

**端点**: `GET /ilink/bot/get_bot_qrcode`

**请求参数**:
```
bot_type=3
```

**完整 URL**:
```
https://ilinkai.weixin.qq.com/ilink/bot/get_bot_qrcode?bot_type=3
```

**响应数据**:
```json
{
  "qrcode": "二维码标识字符串",
  "qrcode_img_content": "二维码图片链接或内容"
}
```

**字段说明**:
- `qrcode`: 二维码唯一标识,用于后续状态查询
- `qrcode_img_content`: 二维码图片内容,可直接显示或生成二维码

---

### 2. 轮询扫码状态

**端点**: `GET /ilink/bot/get_qrcode_status`

**请求参数**:
```
qrcode={二维码标识}
```

**请求头**:
```http
iLink-App-ClientVersion: 1
```

**完整 URL**:
```
https://ilinkai.weixin.qq.com/ilink/bot/get_qrcode_status?qrcode={qrcode}
```

**超时设置**: 35秒

**响应数据**:
```json
{
  "status": "wait|scaned|confirmed|expired",
  "bot_token": "Bearer Token",
  "ilink_bot_id": "Bot ID",
  "baseurl": "API 基础 URL",
  "ilink_user_id": "微信用户 ID"
}
```

**状态说明**:

| 状态 | 说明 | 后续操作 |
|------|------|----------|
| `wait` | 等待扫码 | 继续轮询 |
| `scaned` | 已扫码,等待确认 | 提示用户确认,继续轮询 |
| `confirmed` | 已确认 | 保存凭据,开始消息监听 |
| `expired` | 二维码过期 | 重新获取二维码 |

**凭据保存**:
```json
{
  "token": "bot_token",
  "baseUrl": "baseurl 或默认 URL",
  "accountId": "ilink_bot_id",
  "userId": "ilink_user_id",
  "savedAt": "保存时间 ISO 格式"
}
```

---

## 消息接收

### 3. 长轮询获取消息

**端点**: `POST /ilink/bot/getupdates`

**超时设置**: 35秒 (长轮询)

**请求头**:
```http
Content-Type: application/json
AuthorizationType: ilink_bot_token
X-WECHAT-UIN: {随机UIN}
Authorization: Bearer {token}
Content-Length: {请求体长度}
```

**请求体**:
```json
{
  "get_updates_buf": "上次返回的同步缓冲区,首次为空",
  "base_info": {
    "channel_version": "0.1.0"
  }
}
```

**响应数据**:
```json
{
  "ret": 0,
  "errcode": 0,
  "errmsg": "错误消息",
  "msgs": [
    {
      "from_user_id": "发送者ID",
      "to_user_id": "接收者ID",
      "client_id": "客户端ID",
      "session_id": "会话ID",
      "message_type": 1,
      "message_state": 2,
      "item_list": [...],
      "context_token": "上下文Token",
      "create_time_ms": 1234567890
    }
  ],
  "get_updates_buf": "新的同步缓冲区",
  "longpolling_timeout_ms": 35000
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| `ret` | number | 返回码,0表示成功 |
| `errcode` | number | 错误码,0表示成功 |
| `errmsg` | string | 错误消息 |
| `msgs` | array | 消息列表 |
| `get_updates_buf` | string | 新的同步缓冲区,用于下次请求 |
| `longpolling_timeout_ms` | number | 长轮询超时时间 |

**同步缓冲区机制**:
- 首次请求时 `get_updates_buf` 为空字符串
- 每次请求携带上次返回的 `get_updates_buf`
- 实现增量同步,避免重复获取消息
- 建议持久化存储,支持断线重连

---

## 消息发送

### 4. 发送消息

**端点**: `POST /ilink/bot/sendmessage`

**超时设置**: 15秒

**请求头**:
```http
Content-Type: application/json
AuthorizationType: ilink_bot_token
X-WECHAT-UIN: {随机UIN}
Authorization: Bearer {token}
Content-Length: {请求体长度}
```

**请求体**:
```json
{
  "msg": {
    "from_user_id": "",
    "to_user_id": "接收者ID (xxx@im.wechat格式)",
    "client_id": "客户端ID",
    "message_type": 2,
    "message_state": 2,
    "item_list": [
      {
        "type": 1,
        "text_item": {
          "text": "消息内容"
        }
      }
    ],
    "context_token": "上下文Token"
  },
  "base_info": {
    "channel_version": "0.1.0"
  }
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| `from_user_id` | string | 发送者ID,Bot消息时为空 |
| `to_user_id` | string | 接收者ID,格式为 xxx@im.wechat |
| `client_id` | string | 客户端唯一标识,建议生成唯一ID |
| `message_type` | number | 消息类型: 2=Bot消息 |
| `message_state` | number | 消息状态: 2=完成 |
| `item_list` | array | 消息项列表 |
| `context_token` | string | 上下文Token,从接收的消息中获取 |

**客户端ID生成建议**:
```typescript
function generateClientId(): string {
  return `client:${Date.now()}-${crypto.randomBytes(4).toString("hex")}`;
}
```

---

## 数据结构

### 消息结构 (WeixinMessage)

```typescript
interface WeixinMessage {
  from_user_id?: string;      // 发送者ID (xxx@im.wechat)
  to_user_id?: string;        // 接收者ID
  client_id?: string;         // 客户端ID
  session_id?: string;        // 会话ID
  message_type?: number;      // 消息类型
  message_state?: number;     // 消息状态
  item_list?: MessageItem[];  // 消息项列表
  context_token?: string;     // 上下文Token
  create_time_ms?: number;    // 创建时间戳(毫秒)
}
```

### 消息项结构 (MessageItem)

```typescript
interface MessageItem {
  type?: number;              // 消息项类型
  text_item?: TextItem;       // 文本消息
  voice_item?: VoiceItem;     // 语音消息
  ref_msg?: RefMessage;       // 引用消息
}

interface TextItem {
  text?: string;              // 文本内容
}

interface VoiceItem {
  text?: string;              // 语音转文字内容
}

interface RefMessage {
  message_item?: MessageItem; // 被引用的消息
  title?: string;             // 引用标题
}
```

### 消息类型常量

```typescript
// 消息类型
const MSG_TYPE_USER = 1;      // 用户消息
const MSG_TYPE_BOT = 2;       // Bot消息

// 消息项类型
const MSG_ITEM_TEXT = 1;      // 文本消息项
const MSG_ITEM_VOICE = 3;     // 语音消息项

// 消息状态
const MSG_STATE_FINISH = 2;   // 消息完成状态
```

### 消息内容提取示例

```typescript
function extractTextFromMessage(msg: WeixinMessage): string {
  if (!msg.item_list?.length) return "";
  
  for (const item of msg.item_list) {
    // 文本消息
    if (item.type === 1 && item.text_item?.text) {
      const text = item.text_item.text;
      const ref = item.ref_msg;
      
      // 处理引用消息
      if (ref) {
        const parts: string[] = [];
        if (ref.title) parts.push(ref.title);
        if (parts.length) {
          return `[引用: ${parts.join(" | ")}]\n${text}`;
        }
      }
      return text;
    }
    
    // 语音消息(已转文字)
    if (item.type === 3 && item.voice_item?.text) {
      return item.voice_item.text;
    }
  }
  
  return "";
}
```

---

## 错误处理

### 错误判断

```typescript
const isError = 
  (resp.ret !== undefined && resp.ret !== 0) ||
  (resp.errcode !== undefined && resp.errcode !== 0);
```

### 重试策略

**推荐配置**:
```typescript
const MAX_CONSECUTIVE_FAILURES = 3;  // 最大连续失败次数
const BACKOFF_DELAY_MS = 30000;      // 退避延迟(毫秒)
const RETRY_DELAY_MS = 2000;         // 重试延迟(毫秒)
```

**重试逻辑**:
```typescript
let consecutiveFailures = 0;

while (true) {
  try {
    const resp = await getUpdates();
    
    if (isError) {
      consecutiveFailures++;
      
      if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
        // 达到阈值,长时间退避
        await sleep(BACKOFF_DELAY_MS);
        consecutiveFailures = 0;
      } else {
        // 短时间重试
        await sleep(RETRY_DELAY_MS);
      }
      continue;
    }
    
    consecutiveFailures = 0;
    // 处理消息...
    
  } catch (err) {
    consecutiveFailures++;
    // 错误处理...
  }
}
```

### 超时处理

**AbortError 处理**:
```typescript
try {
  const resp = await getUpdates();
} catch (err) {
  if (err instanceof Error && err.name === 'AbortError') {
    // 长轮询超时,正常情况
    return { ret: 0, msgs: [], get_updates_buf: lastBuf };
  }
  throw err;
}
```

---

## 最佳实践

### 1. 凭据管理

**存储位置**: `~/.claude/channels/wechat/account.json`

**文件权限**: 600 (仅所有者可读写)

```typescript
// 保存凭据
fs.mkdirSync(credentialsDir, { recursive: true });
fs.writeFileSync(credentialsFile, JSON.stringify(data, null, 2));
fs.chmodSync(credentialsFile, 0o600);
```

### 2. Context Token 缓存

```typescript
const contextTokenCache = new Map<string, string>();

// 接收消息时缓存
if (msg.context_token) {
  contextTokenCache.set(msg.from_user_id, msg.context_token);
}

// 发送消息时获取
const contextToken = contextTokenCache.get(userId);
if (!contextToken) {
  throw new Error('No context token for user');
}
```

### 3. 同步缓冲区持久化

```typescript
const syncBufFile = path.join(credentialsDir, 'sync_buf.txt');

// 保存
if (resp.get_updates_buf) {
  fs.writeFileSync(syncBufFile, resp.get_updates_buf);
}

// 恢复
if (fs.existsSync(syncBufFile)) {
  getUpdatesBuf = fs.readFileSync(syncBufFile, 'utf-8');
}
```

### 4. 消息过滤

```typescript
// 只处理用户消息
if (msg.message_type !== MSG_TYPE_USER) {
  continue;
}

// 提取有效文本
const text = extractTextFromMessage(msg);
if (!text) {
  continue;
}
```

### 5. 用户ID处理

```typescript
// 提取用户名 (去掉 @im.wechat 后缀)
const sender = senderId.split('@')[0] || senderId;
```

---

## 完整工作流程

### 认证流程

```
┌─────────────────────────────────────────────────────────┐
│  1. 调用 get_bot_qrcode 获取二维码                      │
│  2. 显示二维码,等待用户扫码                              │
│  3. 循环调用 get_qrcode_status 轮询状态                 │
│     - wait: 继续等待                                    │
│     - scaned: 提示用户确认                              │
│     - confirmed: 保存凭据,进入消息监听                  │
│     - expired: 重新获取二维码                           │
└─────────────────────────────────────────────────────────┘
```

### 消息接收流程

```
┌─────────────────────────────────────────────────────────┐
│  循环:                                                  │
│    1. 调用 getupdates (携带 get_updates_buf)           │
│    2. 更新 get_updates_buf                             │
│    3. 持久化同步缓冲区                                  │
│    4. 遍历 msgs 数组                                    │
│       - 过滤用户消息 (message_type=1)                   │
│       - 提取文本/语音内容                               │
│       - 缓存 context_token                             │
│       - 处理消息                                        │
└─────────────────────────────────────────────────────────┘
```

### 消息发送流程

```
┌─────────────────────────────────────────────────────────┐
│  1. 获取用户对应的 context_token                        │
│  2. 生成唯一的 client_id                                │
│  3. 构造消息请求体                                      │
│  4. 调用 sendmessage                                   │
│  5. 处理响应结果                                        │
└─────────────────────────────────────────────────────────┘
```

---

## 常见问题

### Q1: Context Token 丢失怎么办?

**A**: Context Token 必须从用户发送的消息中获取。如果丢失,需要等待用户发送新消息。

### Q2: 同步缓冲区的作用?

**A**: 实现增量同步,避免重复获取已处理的消息。建议持久化存储,支持断线重连。

### Q3: 长轮询超时是错误吗?

**A**: 不是。35秒超时是正常的长轮询机制,返回空消息列表即可。

### Q4: 如何处理多用户?

**A**: 使用 Map 缓存每个用户的 context_token,以 `from_user_id` 为键。

### Q5: 消息发送失败怎么办?

**A**: 检查 context_token 是否有效,用户是否在线,网络连接是否正常。

---

## 技术支持

- **官方 SDK**: `@tencent-weixin/openclaw-weixin`
- **协议版本**: channel_version: 0.1.0
- **API 基础 URL**: https://ilinkai.weixin.qq.com

---

## 更新日志

### v0.1.0
- 初始版本
- 支持二维码认证
- 支持消息收发
- 支持文本和语音消息
- 支持引用消息

---

**文档生成时间**: 2026-02-24  
**协议版本**: 0.1.0  
**作者**: zc
