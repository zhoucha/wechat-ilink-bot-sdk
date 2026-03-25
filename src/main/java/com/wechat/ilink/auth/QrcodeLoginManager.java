package com.wechat.ilink.auth;

import com.wechat.ilink.api.IlinkApiClient;
import com.wechat.ilink.config.SdkConfig;
import com.wechat.ilink.exception.AuthException;
import com.wechat.ilink.exception.QrcodeTimeoutException;
import com.wechat.ilink.model.auth.Credentials;
import com.wechat.ilink.model.response.QrcodeResponse;
import com.wechat.ilink.model.response.QrcodeStatus;
import com.wechat.ilink.model.response.QrcodeStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 扫码登录管理器
 */
public class QrcodeLoginManager {
    private static final Logger logger = LoggerFactory.getLogger(QrcodeLoginManager.class);
    private final IlinkApiClient apiClient;
    private final SdkConfig config;

    private final ScheduledExecutorService executor;


    public QrcodeLoginManager(IlinkApiClient apiClient, SdkConfig config) {
        this.apiClient = apiClient;
        this.config = config;
        // this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 获取登录二维码
     *
     * @return 二维码标识
     */
    public QrcodeResponse getQrcode() throws IOException {
        return apiClient.getBotQrcode(SdkConfig.BOT_TYPE, Duration.ofSeconds(15));
    }

    /**
     * 轮询二维码状态直到确认或过期
     *
     * @param qrcodeToken 二维码标识
     * @param callback    状态回调
     * @return 登录成功的凭据
     */
    public Credentials pollQrcodeStatus(String qrcodeToken, QrcodeStatusCallback callback) throws IOException, InterruptedException, AuthException, QrcodeTimeoutException {
        long timeout = config.getQrcodeTimeoutMs();
        long pollInterval = config.getQrcodePollIntervalMs();

        CompletableFuture<Credentials> future = new CompletableFuture<>();
        // 记录上次状态，用于检测变化和避免重复触发 onScanned
        AtomicReference<QrcodeStatus> lastStatus = new AtomicReference<>(null);
        AtomicReference<Runnable> taskRef = new AtomicReference<>();
        Runnable pollTask = () -> {
            // ... 内部使用 taskRef.get() 来获取自身
            Runnable thisTask = taskRef.get();
            // 检查线程是否被中断
            if (Thread.currentThread().isInterrupted()) {
                future.completeExceptionally(new InterruptedException());
                return;
            }
            try {
                QrcodeStatusResponse response = apiClient.getQrcodeStatus(qrcodeToken, Duration.ofSeconds(35));
                if (!response.isSuccess()) {
                    future.completeExceptionally(new AuthException(
                            "获取二维码失败 status: " + response.getErrorInfo() + ", token=" + qrcodeToken));
                    return;
                }
                QrcodeStatus currentStatus = response.getStatusEnum();
                // 处理状态变化回调（仅当状态改变且不为 null 时）
                if (currentStatus != null && !currentStatus.equals(lastStatus.get())) {
                    lastStatus.set(currentStatus);
                    if (callback != null) {
                        try {
                            callback.onStatusChanged(currentStatus, response);
                        } catch (Exception e) {
                            // 记录日志，不中断主流程（此处可用日志框架）
                            logger.error("Error in QrcodeStatusCallback.onStatusChanged", e);
                        }
                    }
                }
                switch (currentStatus) {
                    case WAIT:
                    case null:
                        break;
                    case SCANED:
                        // 确保只触发一次 onScanned（通过 lastStatus 已保证状态变化）
                        if (callback != null) {
                            try {
                                callback.onScanned();
                            } catch (Exception e) {
                                logger.error("QrcodeStatusCallback.onScanned error", e);
                            }
                        }
                        break;
                    case CONFIRMED:
                        // 已经确认
                        Credentials credentials = new Credentials(
                                response.getBotToken(),
                                response.getBaseUrl() != null ? response.getBaseUrl() : apiClient.getBaseUrl(),
                                response.getIlinkBotId(),
                                response.getIlinkUserId()
                        );
                        // 先通知回调，再设置凭据（回调异常不影响凭据设置）
                        if (callback != null) {
                            try {
                                callback.onSuccess(credentials);
                            } catch (Exception e) {
                                logger.error("Error in QrcodeStatusCallback.onSuccess", e);
                            }
                        }
                        // 设置凭据到 API 客户端
                        apiClient.setCredentials(credentials);
                        // 完成 future
                        future.complete(credentials);
                        return;
                    case EXPIRED:
                        if (callback != null) {
                            try {
                                callback.onExpired();
                            } catch (Exception e) {
                                logger.error("已经过期，请重新扫码", e);
                            }
                        }
                        future.completeExceptionally(new QrcodeTimeoutException("QR code expired, please scan again"));
                        return;
                }
                // 其他状态（WAIT 或 null）继续轮询，无需特殊处理
                // 调度下一次轮询
                if (!future.isDone()) {
                    executor.schedule(thisTask, pollInterval, TimeUnit.MILLISECONDS);
                }

            } catch (Exception e) {
                // 捕获轮询过程中的异常（如网络异常），完成 future 并抛出
                future.completeExceptionally(e);
            }

        };
        // 启动第一次轮询
        taskRef.set(pollTask);
        executor.execute(pollTask);
        try {
            // 等待结果，超时则抛出超时异常
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // 取消未完成的任务
            throw new QrcodeTimeoutException("QR code scan timeout after " + timeout + "ms");
        } catch (ExecutionException e) {
            // 将内部异常解包并抛出
            Throwable cause = e.getCause();
            switch (cause) {
                case IOException ioException -> throw ioException;
                case AuthException authException -> throw authException;
                case QrcodeTimeoutException qrcodeTimeoutException -> throw qrcodeTimeoutException;
                case InterruptedException interruptedException -> throw interruptedException;
                case null, default -> throw new RemoteException("Unexpected error", cause);
            }
        } finally {
            // 关闭线程池
            executor.shutdownNow();
        }
//        while (System.currentTimeMillis() - startTime < timeout) {
//            QrcodeStatusResponse response = apiClient.getQrcodeStatus(qrcodeToken, Duration.ofSeconds(35));
//            if (!response.isSuccess()) {
//                throw new AuthException("Failed to get qrcode status: " + response.getErrorInfo());
//            }
//            QrcodeStatus status = response.getStatusEnum();
//            if (callback != null) {
//                callback.onStatusChanged(status, response);
//            }
//            if (status == null) {
//                Thread.sleep(pollInterval);
//                continue;
//            }
//            switch (status) {
//                case CONFIRMED:
//                    // 创建凭据
//                    Credentials credentials = new Credentials(
//                            response.getBotToken(),
//                            response.getBaseUrl() != null ? response.getBaseUrl() : apiClient.getBaseUrl(),
//                            response.getIlinkBotId(),
//                            response.getIlinkUserId()
//                    );
//                    // 设置凭据到API客户端
//                    apiClient.setCredentials(credentials);
//                    if (callback != null) {
//                        callback.onSuccess(credentials);
//                    }
//                    return credentials;
//                case EXPIRED:
//                    if (callback != null) {
//                        callback.onExpired();
//                    }
//                    throw new QrcodeTimeoutException("QR code expired, please scan again");
//
//                case SCANED:
//                    if (callback != null) {
//                        callback.onScanned();
//                    }
//                    break;
//
//                case WAIT:
//                default:
//                    break;
//            }
//
//            Thread.sleep(pollInterval);
//        }
//
//        throw new QrcodeTimeoutException("QR code scan timeout after " + timeout + "ms");
    }

    /**
     * 二维码状态回调接口
     */
    public interface QrcodeStatusCallback {
        /**
         * 扫码状态已改变
         *
         * @param status   最新状态
         * @param response 状态响应
         */
        void onStatusChanged(QrcodeStatus status, QrcodeStatusResponse response);

        /**
         * 扫码已确认
         */
        void onScanned();

        /**
         * 扫码成功
         */
        void onSuccess(Credentials credentials);

        /**
         * 二维码已过期
         */
        void onExpired();
    }
}
