package com.wechat.ilink.model.auth;

import java.time.Instant;

/**
 * 凭据信息
 */
public class Credentials {

    private String token;
    private String baseUrl;
    private String accountId;
    private String userId;
    private Instant savedAt;

    public Credentials() {
    }

    public Credentials(String token, String baseUrl, String accountId, String userId) {
        this.token = token;
        this.baseUrl = baseUrl;
        this.accountId = accountId;
        this.userId = userId;
        this.savedAt = Instant.now();
    }

    public boolean isValid() {
        return token != null && !token.isEmpty() &&
               baseUrl != null && !baseUrl.isEmpty() &&
               accountId != null && !accountId.isEmpty();
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Instant savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public String toString() {
        return "Credentials{accountId='" + accountId + "', userId='" + userId + "', savedAt=" + savedAt + "}";
    }
}
