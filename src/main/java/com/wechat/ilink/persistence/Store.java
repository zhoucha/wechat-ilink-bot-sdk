package com.wechat.ilink.persistence;

import com.wechat.ilink.model.auth.Credentials;

import java.io.IOException;

/**
 * 凭据存储
 */
public interface Store {


    void save(Credentials credentials) throws IOException;

    /**
     * 加载凭据
     */
    Credentials load() throws IOException;

    /**
     * 清除凭据
     */
    void clear() throws IOException;

    /**
     * 检查凭据是否存在
     */
    default boolean exists() {
        return false;
    }
}
