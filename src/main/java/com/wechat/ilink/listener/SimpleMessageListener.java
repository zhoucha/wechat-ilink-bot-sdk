package com.wechat.ilink.listener;

import com.wechat.ilink.model.message.WechatMessage;

import java.util.List;

/**
 * 简单消息监听器适配器
 */
public abstract class SimpleMessageListener implements MessageListener {

    @Override
    public void onMessagesReceived(List<WechatMessage> messages) {
        // 默认空实现
    }

    @Override
    public void onPollingTimeout() {
        // 默认空实现
    }

    @Override
    public void onError(Exception error) {
        // 默认空实现
    }

    @Override
    public void onDisconnected() {
        // 默认空实现
    }

    @Override
    public void onReconnected() {
        // 默认空实现
    }
}
