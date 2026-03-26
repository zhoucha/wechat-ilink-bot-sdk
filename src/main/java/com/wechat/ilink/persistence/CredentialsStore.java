package com.wechat.ilink.persistence;

import com.wechat.ilink.model.auth.Credentials;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 凭据存储
 */
public class CredentialsStore implements Store {

    private final Path credentialsFile;
    private final ObjectMapper objectMapper;

    public CredentialsStore(String configDir) {
        this.credentialsFile = Paths.get(configDir, "account.json");
        // Jackson 3.x
        this.objectMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                //.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                //.enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
                //允许字符串使用单引号
                //.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                //允许数字以零开头
                .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
                //允许字符串中包含未转义的控制字符（如 ASCII 0–31，包括换行符、制表符等）
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .findAndAddModules()
                .build();
    }

    /**
     * 保存凭据
     */
    @Override
    public void save(Credentials credentials) throws IOException {
        // 确保目录存在
        Files.createDirectories(credentialsFile.getParent());
        // 写入文件
        objectMapper.writeValue(credentialsFile.toFile(), credentials);
        // 设置文件权限为600（仅所有者可读写）
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-------");
            Files.setPosixFilePermissions(credentialsFile, permissions);
        } catch (UnsupportedOperationException e) {
            // Windows 不支持 POSIX 权限，忽略
        }
    }

    /**
     * 加载凭据
     */
    @Override
    public Credentials load() throws IOException {
        if (!Files.exists(credentialsFile)) {
            return null;
        }
        return objectMapper.readValue(credentialsFile.toFile(), Credentials.class);
    }

    /**
     * 清除凭据
     */
    @Override
    public void clear() throws IOException {
        if (Files.exists(credentialsFile)) {
            Files.delete(credentialsFile);
        }
    }

    /**
     * 检查凭据是否存在
     */
    @Override
    public boolean exists() {
        return Files.exists(credentialsFile);
    }
}
