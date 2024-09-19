package netty.wconfig.client.agentclient.session;
import com.github.rholder.retry.*;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.common.lang.Nullable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.WConfigCallback;
import netty.wconfig.client.agentclient.SubscriptionContext;
import netty.wconfig.client.agentclient.WConfigAgentClient;
import netty.wconfig.client.agentclient.processors.ConfigResponseProcessor;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.agentclient.protocol.requests.*;
import netty.wconfig.client.agentclient.protocol.responses.BaseResponse;
import netty.wconfig.client.agentclient.protocol.responses.CommonResponse;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.agentclient.protocol.responses.DirResponse;
import netty.wconfig.client.configs.ClientConfig;
import netty.wconfig.client.enums.EnumClientMessageType;
import netty.wconfig.client.enums.EnumSubType;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.exceptions.WConfigClientException;
import netty.wconfig.client.exceptions.WConfigClientTimeoutException;
import netty.wconfig.client.utils.LocalFileUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static netty.wconfig.client.constants.ClientConstants.CONFIG_LOCATOR_DELIMITER;

@Slf4j
@RequiredArgsConstructor
public class AgentClient implements WConfigAgentClient {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static final AtomicBoolean CONNECTED = new AtomicBoolean(false);

    private static final AtomicBoolean DESTROYED = new AtomicBoolean(false);

    private static final AtomicInteger requestId = new AtomicInteger(1);

    private static final int MAX_REQUEST_ID = 1024 * 1024 * 1024;

    private final Bootstrap bootstrap;

    private final NioEventLoopGroup nioGroup;

    private final InetSocketAddress agentAddress;

    private final InetSocketAddress agentAddress2nd;

    private final ClientConfig clientConfig;

    private final Map<String, ScheduledFuture<?>> subSchedule = new ConcurrentHashMap<>();

    private final Map<String, SubscriptionContext> subRecords = new ConcurrentHashMap<>();

    private final Map<String, SubscriptionContext> downTimeSubRecords = new ConcurrentHashMap<>();

    private final Cache<String, ConfigResponse> configCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6L, TimeUnit.MINUTES)
            .removalListener((RemovalListener<String, ConfigResponse>) notification -> {
                // 不处理手动移除
                if (!notification.wasEvicted()) {
                    return;
                }

                String namespace = notification.getKey();
                ConfigResponse oldResp = notification.getValue();
                log.trace("wconfig client config-cache evicted, namespace: {}, cachedResponse: {}", namespace, oldResp);
                try {
                    // config-cache 到期时，发送 un-sub 指令到 agent
                    unSubscribeToAgent(notification.getKey());
                } catch (WConfigClientException e) {
                    log.error("wconfig client config cache eviction error", e);
                }
            }).build();

    private final Retryer<?> authRetryer = RetryerBuilder.newBuilder()
            .retryIfException()
            .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
            .withStopStrategy(new StopStrategy() {
                @Override
                public boolean shouldStop(Attempt failedAttempt) {
                    log.warn("wconfig client auth failed, secret: {}, retrying...", clientConfig.getSecret(), failedAttempt.getExceptionCause());
                    return !CONNECTED.get();
                }
            })
            .build();

    private ScheduledExecutorService blockingScheduler;

    private InetSocketAddress chosenAddress;

    private Channel channel;

    private String clusterName;

    /**
     *
     */
    @Override
    public ConfigResponse getConfigCache(@NonNull String namespace) {
        return configCache.getIfPresent(namespace);
    }

    public Long getLatestModifiedTimestamp(@NonNull String namespace) {
        ConfigResponse configResponse = getConfigCache(namespace);
        return Objects.isNull(configResponse) ? -1 : configResponse.getLatestModifiedTimestamp();
    }

    public Integer getLatestVersion(@NonNull String namespace) {
        ConfigResponse configResponse = getConfigCache(namespace);
        return Objects.isNull(configResponse) ? -1 : configResponse.getVersion();
    }

    /**
     *
     */
    @Override
    public boolean putConfigCache(@NonNull String namespace, @NonNull ConfigResponse newConfig) {
        ConfigResponse cachedResponse = configCache.getIfPresent(namespace);
        if (Objects.nonNull(cachedResponse)) {
            if (cachedResponse.getLatestModifiedTimestamp() == newConfig.getLatestModifiedTimestamp()) {
                log.trace("wconfig client cached modifiedTimestamp not changed, namespace: {}, cached: {}, received: {}", namespace, cachedResponse.getLatestModifiedTimestamp(), newConfig.getLatestModifiedTimestamp());
                return false;
            } else if (cachedResponse.getLatestModifiedTimestamp() > newConfig.getLatestModifiedTimestamp()) {
                log.warn("wconfig client cached version is greater than newly received version, namespace: {}, cached: {}, received: {}",
                        namespace, cachedResponse.getLatestModifiedTimestamp(), newConfig.getLatestModifiedTimestamp());
                return false;
            }
            if (cachedResponse.getVersion() == newConfig.getVersion()) {
                log.trace("wconfig client cached version not changed, namespace: {}, cached: {}, received: {}", namespace, cachedResponse.getVersion(), newConfig.getVersion());
                return false;
            }
        }

        configCache.put(namespace, newConfig);
        log.trace("wconfig client config-cache added, namespace: {}, cachedResponse: {}", namespace, newConfig);

        return true;
    }

    /**
     *
     */
    @Override
    public boolean isSubscribed(@NonNull String namespace) {
        return subSchedule.containsKey(namespace);
    }

    /**
     *
     */
    @Override
    public SubscriptionContext getSubContext(@NonNull String namespace) {
        return subRecords.get(namespace);
    }

    /**
     *
     */
    @Override
    public void cacheSubSchedule(@NonNull String namespace,
                                 @Nullable ScheduledFuture<?> future,
                                 @NonNull EnumSubType subType,
                                 @NonNull SubscriptionContext context) {
        if (Objects.nonNull(future)) {
            subSchedule.put(namespace, future);
        }

        switch (subType) {
            case GET:
                if (Objects.nonNull(subRecords.putIfAbsent(namespace, context))) {
                    // 已存在，不更新，不打印后续日志，结束
                    return;
                }
                break;
            case SCHEDULE:
                // 周期订阅不再更新订阅缓存
                return;
            default:
                // 常规 sub 调用需要添加/更新订阅缓存
                SubscriptionContext cachedContext = subRecords.get(namespace);
                if (Objects.nonNull(cachedContext)) {
                    cachedContext.getCallBackSet().addAll(context.getCallBackSet());
                } else {
                    subRecords.put(namespace, context);
                }
        }

        log.trace("wconfig client sub schedule cached, namespace: {}, context: {}", namespace, context);
    }

    /**
     *
     */
    @Override
    public void cancelSubSchedule(@NonNull String namespace) {
        Optional.ofNullable(subSchedule.remove(namespace))
                .ifPresent((Consumer<ScheduledFuture<?>>) future -> future.cancel(false));
        subRecords.remove(namespace);
        log.trace("wconfig client sub schedule canceled, namespace: {}", namespace);
    }

    /**
     * 获取当前连接的 cluster-name
     */
    @Override
    public String getClusterName() {
        return Strings.isNullOrEmpty(clusterName) ? clientConfig.getLocalDevCluster().trim() : clusterName;
    }

    @Override
    public String getGroupName() {
        return clientConfig.getGroupName().trim();
    }

    @Override
    public String getSecret() {
        return clientConfig.getSecret().trim();
    }

    @Override
    public boolean isDestroyed() {
        return DESTROYED.get();
    }

    @PreDestroy
    @Override
    public void destroy() {
        DESTROYED.set(true);
        blockingScheduler.shutdownNow();
        nioGroup.shutdownGracefully();
        log.info("wconfig agent-client destroyed");
    }

    @PostConstruct
    @Override
    public void afterPropertiesSet() throws Exception {

        // 初始化一次
        if (!INITIALIZED.compareAndSet(false, true)) {
            String errMsg = "wconfig client agent connection has already established";
            log.warn(errMsg);
            throw new WConfigClientException(errMsg);
        }
        connect(1);
        // fixme: 重构解决循环依赖（AgentClient <-> ClientInboundHandler）
        ConfigResponseProcessor.setAgentClient(this);
    }

    private void connect(Integer retryDelayInSec) {
        if (isDestroyed()) return;

        try {
            chosenAddress = chosenAddress == agentAddress ? agentAddress2nd : agentAddress;
            channel = bootstrap.connect(chosenAddress).addListener((ChannelFutureListener) onCompletion -> {
                if (onCompletion.isSuccess()) {
                    log.info("wconfig client agent connected at {}", chosenAddress);
                    CONNECTED.set(true);

                    AgentClient.this.createScheduler();
                    // 连接成功后周期心跳
                    blockingScheduler.scheduleAtFixedRate(AgentClient.this::pingSchedule, 1, 10, TimeUnit.SECONDS);
                    // 过期配置缓存周期清理
                    blockingScheduler.scheduleAtFixedRate(configCache::cleanUp, 5, 5, TimeUnit.MINUTES);
                } else {
                    log.warn("wconfig client failed to connect to agent at {}, retry...", chosenAddress);
                    // 连接失败 1 秒后重试, 不要将其他阻塞任务置于 nio-loop 中
                    int nextRetryDelayInSec;
                    if (retryDelayInSec < 30) {
                        nextRetryDelayInSec = retryDelayInSec + retryDelayInSec;
                    } else {
                        nextRetryDelayInSec = retryDelayInSec;
                    }
                    onCompletion.channel().eventLoop().schedule(() -> connect(nextRetryDelayInSec), retryDelayInSec, TimeUnit.SECONDS);
                }
            }).sync().channel();
        } catch (Throwable e) {
            log.warn("wconfig client init error", e);
            return;
        }

        try {
            authRetryer.call(() -> {
                checkAuth();
                return null;
            });
        } catch (Throwable t) {
            log.error("wconfig client auth failed, secret: {}", clientConfig.getSecret(), t);
            destroy();
            return;
        }

        // channel close future listener
        channel.closeFuture().addListener((ChannelFutureListener) onClosed -> {
            log.info("wconfig client agent channel at {} closed", chosenAddress);
            CONNECTED.set(false);
            blockingScheduler.shutdownNow();
            // agent 断开事件时重连
            connect(1);
        });

        recoverAfterReconnected();
    }

    private void createScheduler() {
        blockingScheduler = Executors.newScheduledThreadPool(1,
                new ThreadFactoryBuilder().setNameFormat("WConfig_Client_Schedule").setDaemon(true).build());
    }

    private void checkAuth() throws WConfigClientException, WConfigClientTimeoutException {
        log.info("wconfig client start to get auth. secret:{}", clientConfig.getSecret());
        AuthRequest authRequest = AuthRequest.builder()
                .secret(clientConfig.getSecret())
                .sdkVersion(readPomVersion())
                .build();
        CommonResponse response = (CommonResponse) send(authRequest, null);
        if (Objects.isNull(response)) {
            throw new WConfigClientException("wconfig client auth response is null");
        }

        if (response.getCode() == 0) {
            // 所有后续请求都是同一集群
            clusterName = response.getValue();
            log.info("wconfig client authorized. secret:{} cluster:{}", clientConfig.getSecret(), clusterName);
        } else {
            throw new WConfigClientException(String.format("un-authorized:%s", response.getValue()));
        }
    }

    private String readPomVersion() {
        Properties props = new Properties();
        String version = "new_";
        try {
            InputStream pom = getClass().getResourceAsStream("/META-INF/maven/com.bj58.wconfig/wconfig-client/pom.properties");
            if (Objects.isNull(pom)) {
                version += "local_dev";
                return version;
            }

            props.load(pom);
            version += props.getProperty("version");
            log.info("wconfig client version is " + version);
        } catch (Exception e) {
            log.error("wconfig client get version error.", e);
        }

        return version;
    }

    private void pingSchedule() {
        try {
            BaseResponse pingResponse = send(PingRequest.builder().build(), null);
            if (pingResponse.getType() != EnumClientMessageType.PING_RESP) {
                throw new WConfigClientException("no response for ping");
            }
            log.trace("wconfig client ping responded from agent {}", channel.remoteAddress());
        } catch (Throwable t) {
            log.error("wconfig client heart beat error", t);
        }
    }

    private void recoverAfterReconnected() {
        // 断开连接后恢复 subscription
        subRecords.forEach((namespace, context) -> {
            log.info("recover subscribed namespace {}, context {}", namespace, context);

            context.getCallBackSet().forEach(callback -> {
                try {
                    subscribeToAgent(namespace, getLatestModifiedTimestamp(namespace), getLatestVersion(namespace), callback, context.getLocalFile(), context.getFileType(), EnumSubType.DEFAULT);
                } catch (WConfigClientException e) {
                    log.error("wconfig client recovery subscribe error", e);
                }
            });
        });

        // 对未连接期间的 subscription 进行重试
        downTimeSubRecords.forEach((namespace, context) -> {
            log.trace("retry down-time-called subscriptions, namespace {}, context {}", namespace, context);
            context.getCallBackSet().forEach(callback -> {
                try {
                    subscribeToAgent(namespace, getLatestModifiedTimestamp(namespace), getLatestVersion(namespace), callback, context.getLocalFile(), context.getFileType(), EnumSubType.DEFAULT);
                } catch (WConfigClientException e) {
                    log.error("wconfig client retry downtime-ordered subscriptions error", e);
                }
            });
        });
    }

    /**
     *
     */
    @Override
    public ConfigResponse subscribeToAgent(@NonNull String namespace,
                                           @NonNull Long latestModifiedTimestamp,
                                           @NonNull Integer latestVersion,   // sub 之前版本
                                           @Nullable WConfigCallback callback,
                                           @NonNull String localFile,
                                           @NonNull EnumWConfigFileType fileType,
                                           @NonNull EnumSubType subType) throws WConfigClientException {
        if (!CONNECTED.get()) {
            log.warn("wconfig client is not connected to agent, abort subscription, and will retry once connected again, local file will be returned");
            SubscriptionContext context = SubscriptionContext.builder()
                    .callBackSet(new HashSet<>(Arrays.asList(callback)))
                    .localFile(localFile)
                    .fileType(fileType)
                    .build();
            SubscriptionContext record = downTimeSubRecords.get(namespace);
            if (Objects.nonNull(record)) {
                context.getCallBackSet().addAll(record.getCallBackSet());
            }
            downTimeSubRecords.put(namespace, context);

            ConfigResponse localResponse = ConfigResponse.builder().config(LocalFileUtil.readFile(fileType, localFile)).build();
            ConfigResponseProcessor.processLocalConfig(namespace, localResponse, clientConfig.getEnvironment());
            return localResponse;
        }

        SubRequest subRequest = prepareSubscription(namespace, latestModifiedTimestamp, latestVersion, callback, localFile, fileType, subType);
        BaseResponse response;
        try {
            response = send(subRequest, null);
        } catch (WConfigClientTimeoutException te) {
            log.warn("wconfig client sub timeout, using local cache");
            response = CommonResponse.builder().code(0).build();
        }

        log.trace("wconfig client sub-response received, {}", response);
        switch (response.getType()) {
            case COMMON_RESP:
                CommonResponse commonResponse = (CommonResponse) response;
                if (commonResponse.getCode() != 0) {
                    throw new WConfigClientException(String.format("wconfig client subscribe error, response: %s namespace: %s", commonResponse.getValue(), namespace));
                }
                // agent 返回 errorcode == 0 时，说明配置没有变化或无响应，这里直接续期 config-cache
                log.trace("remote version un-changed, or no response, leasing the local config cache");
                return leaseConfigCache(namespace, localFile, fileType);
            case CONF_RESP:
                log.trace("new version of config has bean released");
                ConfigResponse newConfig = (ConfigResponse) response;
                // 刷新配置缓存
                ConfigResponseProcessor.processNewConfig(
                        getClusterName(), getGroupName(), namespace, newConfig, fileType, localFile, channel, clientConfig.getEnvironment());
                return newConfig;
            default:
                throw new WConfigClientException("wconfig client subscribe response error, invalid type: " + response.getType());
        }
    }

    private SubRequest prepareSubscription(String namespace,
                                           Long latestModifiedTimestamp,
                                           Integer latestVersion,
                                           WConfigCallback callback,
                                           String localFile,
                                           EnumWConfigFileType fileType,
                                           EnumSubType subType) {
        ScheduledFuture<?> future = null;
        if (subType == EnumSubType.DEFAULT) {
            // 用户使用客户端 sub 方法，增加订阅周期任务，后续周期订阅任务 或 get 调用的 sub 不创建周期任务
            future = blockingScheduler.scheduleAtFixedRate(() -> {
                try {
                    Long latestModTime = getLatestModifiedTimestamp(namespace);
                    Integer latestVer = getLatestVersion(namespace);
                    subscribeToAgent(namespace, latestModTime, latestVer, null, localFile, fileType, EnumSubType.SCHEDULE);
                    log.trace("wconfig client scheduled sub, namespace: {}, latestVersion: {}, latestModifiedTimestamp: {}",
                            namespace, latestVer, latestModTime);
                } catch (Throwable t) {
                    log.error("wconfig client scheduled subscription error", t);
                }
            }, 5, 5, TimeUnit.MINUTES);
        }

        cacheSubSchedule(namespace, future, subType, SubscriptionContext.builder()
                .callBackSet(new HashSet<>(Arrays.asList(callback)))
                .localFile(localFile)
                .fileType(fileType)
                .build());

        return SubRequest.builder()
                .clusterName(getClusterName())
                .groupName(getGroupName())
                .namespaceName(namespace)
                .latestModifiedTimestamp(latestModifiedTimestamp)
                .configVersion(latestVersion)
                .build();
    }

    /**
     *
     */
    @Override
    public void unSubscribeToAgent(@NonNull String namespace) throws WConfigClientException {
        sendNoResponse(UnSubRequest.builder()
                .clusterName(getClusterName())
                .groupName(getGroupName())
                .namespaceName(namespace)
                .build());
        cancelSubSchedule(namespace);
    }

    @Override
    public List<String> getDirFromAgent(@NonNull String dirPath) throws WConfigClientException {
        DirRequest dirRequest = DirRequest.builder()
                .clusterName(getClusterName())
                .dirPath(dirPath)
                .build();
        BaseResponse response;
        try {
            response = send(dirRequest, null);
        } catch (WConfigClientTimeoutException e) {
            response = CommonResponse.builder().value("request timeout").build();
        }

        switch (response.getType()) {
            case COMMON_RESP:
                CommonResponse commonResponse = (CommonResponse) response;
                throw new WConfigClientException("wconfig client get-dir error, " + commonResponse.getValue());
            case DIR_RESP:
                DirResponse dirResponse = (DirResponse) response;
                return dirResponse.getChildrenPaths();
            default:
                throw new WConfigClientException("wconfig client get-dir response error, invalid type: " + response.getType());
        }
    }

    public void sendNoResponse(BaseRequest request) {
        channel.writeAndFlush(request.toFrame());
    }

    /**
     * 发送数据
     */
    public BaseResponse send(@NonNull BaseRequest request,
                             @Nullable Integer timeoutInMillis) throws WConfigClientException, WConfigClientTimeoutException {

        int requestId = generateRequestId();
        RequestSessionTracker.submit(requestId);
        try {
            channel.writeAndFlush(request.toFrame(requestId));
        } catch (Throwable t) {
            // agent 连接有任何问题，仍需完成后续逻辑，统一切换为读取本地备份文件
            log.error("", t);
        }

        if (timeoutInMillis == null || timeoutInMillis == 0) {
            timeoutInMillis = clientConfig.getRequestTimeoutInMillis();
        }
        ClientFrame frame = RequestSessionTracker.getRespondedFrame(requestId, timeoutInMillis);
        if (Objects.isNull(frame)) {
            String errMsg = String.format("request timeout for %s milli-seconds, type: %s requestId: %s",
                    timeoutInMillis, request.getType(), requestId);
            log.error(errMsg);
            throw new WConfigClientTimeoutException(errMsg);
        }
        return (BaseResponse) frame.getMessage();
    }

    private int generateRequestId() {
        // request id 0 保留给 ack 类型
        return requestId.getAndAccumulate(1, (c, x) -> c >= MAX_REQUEST_ID ? 1 : c + x);
    }

    private ConfigResponse leaseConfigCache(String namespace, String localFile, EnumWConfigFileType fileType) {
        ConfigResponse cachedResponse = configCache.getIfPresent(namespace);
        if (Objects.isNull(cachedResponse)) {
            // 未命中，从本地备份文件读取，正常情况不应发生这种情况
            String locator = getClusterName() + CONFIG_LOCATOR_DELIMITER + getGroupName() + CONFIG_LOCATOR_DELIMITER + namespace;
            log.error("wconfig client leasing config cache error, read local backup file {} of type {} for {}",
                    localFile, fileType, locator);
            cachedResponse = ConfigResponse.builder()
                    .tripleLocator(locator)
                    .version(-1)
                    .config(LocalFileUtil.readFile(fileType, localFile))
                    .build();
        }
        configCache.put(namespace, cachedResponse);
        log.trace("wconfig client config cache leased, namespace:{}", namespace);
        return cachedResponse;
    }
}

