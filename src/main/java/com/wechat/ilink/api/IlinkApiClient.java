package com.wechat.ilink.api;

import com.wechat.ilink.http.HttpClientWrapper;
import com.wechat.ilink.model.auth.Credentials;
import com.wechat.ilink.model.request.GetUpdatesRequest;
import com.wechat.ilink.model.request.SendMessageRequest;
import com.wechat.ilink.model.response.ApiResponse;
import com.wechat.ilink.model.response.GetUpdatesResponse;
import com.wechat.ilink.model.response.QrcodeResponse;
import com.wechat.ilink.model.response.QrcodeStatusResponse;
import com.wechat.ilink.util.ClientIdGenerator;
import com.wechat.ilink.util.UinGenerator;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * ilink API 客户端
 */
public class IlinkApiClient implements AutoCloseable {

    private final HttpClientWrapper httpClient;
    private final String baseUrl;
    private Credentials credentials;

    public IlinkApiClient(String baseUrl) {
        this.httpClient = new HttpClientWrapper();
        this.baseUrl = baseUrl;
    }

    /**
     * 设置凭据
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * 构建请求头
     */
    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("AuthorizationType", "ilink_bot_token");
        headers.put("X-WECHAT-UIN", UinGenerator.generate());
        headers.put("Content-Type", "application/json");
        if (credentials != null && credentials.getToken() != null) {
            headers.put("Authorization", "Bearer " + credentials.getToken());
        }

        return headers;
    }

    /**
     * 获取二维码
     *
     * @param botType bot类型 (3为ClawBot)
     */
    public QrcodeResponse getBotQrcode(String botType, Duration timeout) throws IOException {
        String url = baseUrl + "/ilink/bot/get_bot_qrcode?bot_type=" + botType;
        Map<String, String> headers = buildHeaders();

        String response = httpClient.get(url, headers, timeout);
        return parseResponse(response, QrcodeResponse.class);
    }

    /**
     * 获取二维码状态
     */
    public QrcodeStatusResponse getQrcodeStatus(String qrcode, Duration timeout) throws IOException {
        String url = baseUrl + "/ilink/bot/get_qrcode_status?qrcode=" + qrcode;
        Map<String, String> headers = buildHeaders();
        headers.put("iLink-App-ClientVersion", "1");

        String response = httpClient.get(url, headers, timeout);
        return parseResponse(response, QrcodeStatusResponse.class);
    }

    /**
     * 获取消息更新（长轮询）
     */
    public GetUpdatesResponse getUpdates(GetUpdatesRequest request, Duration timeout) throws IOException {
        String url = baseUrl + "/ilink/bot/getupdates";
        Map<String, String> headers = buildHeaders();

        String response = httpClient.post(url, headers, request, timeout);
        return parseResponse(response, GetUpdatesResponse.class);
    }

    /**
     * 发送消息
     */
    public ApiResponse sendMessage(SendMessageRequest request, Duration timeout) throws IOException {
        // 设置 client_id
        if (request.getMsg() != null && request.getMsg().getClientId() == null) {
            request.getMsg().setClientId(ClientIdGenerator.generate());
        }

        String url = baseUrl + "/ilink/bot/sendmessage";
        Map<String, String> headers = buildHeaders();

        String response = httpClient.post(url, headers, request, timeout);
        return parseResponse(response, ApiResponse.class);
    }

    private <T> T parseResponse(String json, Class<T> clazz) throws IOException {
        try {
            return httpClient.getObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new IOException("Failed to parse response: " + json.substring(0, Math.min(100, json.length())), e);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
