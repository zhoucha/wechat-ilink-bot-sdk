package com.wechat.ilink.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 同步缓冲区存储
 */
public class SyncBufferStore {

    private final Path bufferFile;
    private String buffer;

    public SyncBufferStore(String configDir) {
        this.bufferFile = Paths.get(configDir, "sync_buf.txt");
        this.buffer = loadFromFile();
    }

    /**
     * 获取当前同步缓冲区值
     */
    public synchronized String getBuffer() {
        return buffer;
    }

    /**
     * 更新同步缓冲区（内存和文件）
     */
    public synchronized void updateBuffer(String newBuffer) throws IOException {
        if (newBuffer == null || newBuffer.isEmpty()) {
            return;
        }
        this.buffer = newBuffer;
        saveToFile();
    }

    /**
     * 清空缓冲区
     */
    public synchronized void clear() throws IOException {
        this.buffer = "";
        if (Files.exists(bufferFile)) {
            Files.delete(bufferFile);
        }
    }

    private String loadFromFile() {
        try {
            if (Files.exists(bufferFile)) {
                return new String(Files.readAllBytes(bufferFile), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // 忽略加载错误，返回空字符串
        }
        return "";
    }

    private void saveToFile() throws IOException {
        Files.createDirectories(bufferFile.getParent());
        Files.write(bufferFile, buffer.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
