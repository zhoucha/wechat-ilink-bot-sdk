package com.wechat.ilink.example;

import com.wechat.ilink.WechatBotSdk;
import com.wechat.ilink.auth.QrcodeLoginManager;
import com.wechat.ilink.listener.SimpleMessageListener;
import com.wechat.ilink.model.auth.Credentials;
import com.wechat.ilink.model.message.WechatMessage;
import com.wechat.ilink.model.response.QrcodeResponse;
import com.wechat.ilink.model.response.QrcodeStatus;
import com.wechat.ilink.model.response.QrcodeStatusResponse;

import java.util.List;

/**
 * SDK 使用示例
 */
public class BotExample {

    public static void main(String[] args) {
        // 创建 SDK 实例
        try (WechatBotSdk sdk = new WechatBotSdk(System.getProperty("user.home") + "/.claude/channels/wechat/")) {

            // 检查是否已登录
            if (!sdk.isAuthenticated()) {
                System.out.println("Not authenticated, getting QR code...");

                // 获取二维码
                QrcodeResponse qrcode = sdk.getQrcode();
                System.out.println("QR Code Token: " + qrcode.getQrcode());
                System.out.println("Please scan the QR code image:");
                System.out.println(qrcode.getQrcodeImgContent());

                // 等待用户扫码确认
                Credentials credentials = sdk.loginWithQrcode(qrcode.getQrcode(), new QrcodeLoginManager.QrcodeStatusCallback() {
                    @Override
                    public void onStatusChanged(QrcodeStatus status, QrcodeStatusResponse response) {
                        System.out.println("Status changed: " + status.getDescription());
                    }

                    @Override
                    public void onScanned() {
                        System.out.println("QR code scanned, waiting for confirmation...");
                    }

                    @Override
                    public void onSuccess(Credentials credentials) {
                        System.out.println("Login successful! Bot ID: " + credentials.getAccountId());
                    }

                    @Override
                    public void onExpired() {
                        System.out.println("QR code expired, please restart.");
                    }
                });

                System.out.println("Logged in as: " + credentials.getUserId());
            }

            // 添加消息监听器
            sdk.addMessageListener(new SimpleMessageListener() {
                @Override
                public void onMessagesReceived(List<WechatMessage> messages) {
                    for (WechatMessage msg : messages) {
                        if (msg.isUserMessage()) {
                            String sender = msg.getSenderName();
                            String content = msg.extractTextContent();
                            System.out.println(sender + ": " + content);
                            // 自动回复
                            try {
                                sdk.sendTextMessage(msg.getFromUserId(), "收到你的消息：" + content);
                            } catch (Exception e) {
                                System.err.println("Failed to send reply: " + e.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onError(Exception error) {
                    System.err.println("Error: " + error.getMessage());
                }

                @Override
                public void onDisconnected() {
                    System.out.println("Disconnected, retrying...");
                }

                @Override
                public void onReconnected() {
                    System.out.println("Reconnected");
                }
            });

            // 启动消息监听
            sdk.startListening();
            System.out.println("Message listener started. Press Ctrl+C to exit.");

            // 保持运行
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
