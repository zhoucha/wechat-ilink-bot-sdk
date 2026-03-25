package com.wechat.ilink.listener;

import com.wechat.ilink.api.IlinkApiClient;
import com.wechat.ilink.model.message.WechatMessage;

import java.util.List;

/**
 * 消息监听器接口
 */
public interface MessageListener {

    /**
     * 收到消息时调用
     *
     * @param messages 消息列表
     */
    void onMessagesReceived(List<WechatMessage> messages);

    /**
     * 长轮询超时时调用（正常现象，表示没有新消息）
     */
    void onPollingTimeout();

    /**
     * 发生错误时调用
     *
     * @param error 错误信息
     */
    void onError(Exception error);

    /**
     * 连接断开时调用
     */
    void onDisconnected();

    /**
     * 连接恢复时调用
     */
    void onReconnected();
}
