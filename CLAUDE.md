# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WeChat iLink Bot SDK for Java - A client SDK for integrating with WeChat's iLink bot platform.

- **Tech Stack**: Java 25, Apache HttpClient 5, Jackson 3.x, SLF4J
- **Build Tool**: Maven
- **Protocol**: iLink API v0.1.0 with ClawBot (bot_type=3)

## Build Commands

Compile the project:
```bash
mvn compile
```

Run tests:
```bash
mvn test
```

Run a single test class:
```bash
mvn test -Dtest=ClassName
```

Run a single test method:
```bash
mvn test -Dtest=ClassName#methodName
```

Package JAR:
```bash
mvn package
```

Clean build:
```bash
mvn clean
```

## Architecture

The SDK follows a layered architecture with clear separation of concerns:

```
WechatBotSdk (Facade)
├── api/IlinkApiClient (HTTP layer)
├── auth/QrcodeLoginManager (Login flow)
├── listener/MessageListenerService (Long polling)
├── sender/MessageSenderService (Message sending)
├── persistence/* (File storage)
└── cache/* (In-memory caches)
```

### Key Components

**WechatBotSdk**: Main entry point providing high-level operations like `loginWithQrcode()`, `sendTextMessage()`, `startListening()`.

**IlinkApiClient**: Low-level HTTP client wrapping Apache HttpClient 5. Handles request headers including:
- `AuthorizationType: ilink_bot_token`
- `X-WECHAT-UIN: {base64 random}` (generated via UinGenerator)
- `Authorization: Bearer {token}`

**MessageListenerService**: Background thread performing long-polling (35s timeout) for incoming messages. Implements retry logic with exponential backoff (3 failures → 30s delay, otherwise 2s delay).

**QrcodeLoginManager**: Manages QR code login flow - get QR code → poll status (wait → scanned → confirmed) → save credentials.

### Data Flow

1. **Authentication**: QR code scan → obtain Bearer token → persist credentials (JSON file with 600 permissions)
2. **Receiving**: Long-poll `getupdates` endpoint → parse messages → filter user messages → cache context_token by from_user_id
3. **Sending**: Lookup context_token → construct message → POST `sendmessage`

### Jackson 3.x Migration Note

Jackson 3.x uses different package names. Import from:
- `tools.jackson.core.*` (was `com.fasterxml.jackson.core`)
- `tools.jackson.databind.*` (was `com.fasterxml.jackson.databind`)
- `tools.jackson.datatype.*` (was `com.fasterxml.jackson.datatype`)

ObjectMapper is typically constructed via:
```java
ObjectMapper mapper = JsonMapper.builder()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .findAndAddModules()
    .build();
```

## Key Files

- `WechatBotSdk.java`: Main SDK class
- `IlinkApiClient.java`: HTTP request handling
- `MessageListenerService.java`: Background polling loop
- `MessageType.java`, `WechatMessage.java`: Core domain models
- `SdkConfig.java`: Configurable timeouts and retry parameters

## Development Workflow

- Source directory: `src/main/java/`
- Test directory: `src/test/java/`
- Example usage in: `src/main/java/com/wechat/ilink/example/BotExample.java`
