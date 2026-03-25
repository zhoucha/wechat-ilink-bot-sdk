package com.wechat.ilink.model.message;

/**
 * 消息类型常量定义
 */
public final class MessageType {

    private MessageType() {
        // 私有构造函数，防止实例化
    }

    /** 用户消息 */
    public static final int USER = 1;

    /** Bot消息 */
    public static final int BOT = 2;

    /**
     * 消息项类型
     */
    public static final class Item {
        private Item() {}

        /** 文本消息项 */
        public static final int TEXT = 1;

        /** 语音消息项 */
        public static final int VOICE = 3;
    }

    /**
     * 消息状态
     */
    public static final class State {
        private State() {}

        /** 消息完成状态 */
        public static final int FINISH = 2;
    }
}
