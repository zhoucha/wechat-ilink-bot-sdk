package com.wechat.ilink.util.http;

import com.wechat.ilink.exception.ApiException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP 客户端包装
 */
public class HttpClientWrapper implements AutoCloseable {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientWrapper() {
        this.httpClient = HttpClients.createDefault();
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
     * 发送 GET 请求
     */
    public String get(String url, Map<String, String> headers, Duration timeout) throws ApiException {
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet, headers, timeout);
    }

    /**
     * 发送 POST 请求
     */
    public String post(String url, Map<String, String> headers, Object body, Duration timeout) throws ApiException {
        HttpPost httpPost = new HttpPost(url);

        if (body != null) {
            String jsonBody = objectMapper.writeValueAsString(body);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }

        return executeRequest(httpPost, headers, timeout);
    }

    private String executeRequest(HttpUriRequestBase request, Map<String, String> headers, Duration timeout) throws ApiException {
        // 设置请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置超时
        if (timeout != null) {
            RequestConfig config = RequestConfig.custom()
                    .setResponseTimeout(Timeout.ofMilliseconds(timeout.toMillis()))
                    .build();
            request.setConfig(config);
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return "";
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            throw new ApiException("Failed to parse response", e);
        }
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
