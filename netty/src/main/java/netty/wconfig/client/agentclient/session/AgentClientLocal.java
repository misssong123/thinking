package netty.wconfig.client.agentclient.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.WConfigCallback;
import netty.wconfig.client.agentclient.SubscriptionContext;
import netty.wconfig.client.agentclient.WConfigAgentClient;
import netty.wconfig.client.agentclient.processors.ConfigResponseProcessor;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.configs.ClientConfig;
import netty.wconfig.client.enums.EnumSubType;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.exceptions.WConfigClientException;
import netty.wconfig.client.utils.LocalFileUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static netty.wconfig.client.constants.ClientConstants.CONFIG_LOCATOR_DELIMITER;

@Slf4j
@RequiredArgsConstructor
public class AgentClientLocal implements WConfigAgentClient {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static final AtomicBoolean DESTROYED = new AtomicBoolean(false);

    private final ClientConfig clientConfig;

    private final Cache</*namespace*/String, /*configs*/ConfigResponse> configCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6L, TimeUnit.SECONDS)
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
                    AgentClientLocal.this.unSubscribeToAgent(notification.getKey());
                } catch (WConfigClientException e) {
                    log.error("wconfig client config cache eviction error", e);
                }
            }).build();

    private final Map<String, ScheduledFuture<?>> subSchedule = new ConcurrentHashMap<>();

    private final Map<String, SubscriptionContext> subRecords = new ConcurrentHashMap<>();

    private final ScheduledExecutorService blockingScheduler = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("WConfig_Client_Schedule").setDaemon(true).build());

    /**
     *
     */
    @Override
    public ConfigResponse getConfigCache(@NonNull String namespace) {
        return configCache.getIfPresent(namespace);
    }

    /**
     * 尝试将 sub 响应放入缓存，远程版本大于本地版本时成功，等于则取消，小于则为异常
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

    @Override
    public SubscriptionContext getSubContext(@NonNull String namespace) {
        return subRecords.get(namespace);
    }

    @Override
    public void cacheSubSchedule(@NonNull String namespace,
                                 @NonNull ScheduledFuture<?> future,
                                 @NonNull EnumSubType subType,
                                 @NonNull SubscriptionContext context) {
        if (Objects.nonNull(future)) {
            subSchedule.put(namespace, future);
        }

        // 常规 sub 调用需要添加/更新订阅缓存
        SubscriptionContext cachedContext = subRecords.get(namespace);
        if (Objects.nonNull(cachedContext)) {
            context.getCallBackSet().addAll(cachedContext.getCallBackSet());
        } else {
            subRecords.put(namespace, context);
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
        return clientConfig.getLocalDevCluster();
    }

    @Override
    public String getGroupName() {
        return clientConfig.getGroupName().trim();
    }

    @Override
    public String getSecret() {
        return "local_dev_secret";
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
        log.info("wconfig agent-client (dev mode) destroyed");
    }

    @PostConstruct
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化一次
        if (!INITIALIZED.compareAndSet(false, true)) {
            String errMsg = "wconfig client (dev mode) has already been created";
            log.warn(errMsg);
            throw new WConfigClientException(errMsg);
        }
        ConfigResponseProcessor.setAgentClient(this);

        blockingScheduler.scheduleAtFixedRate(configCache::cleanUp, 5, 5, TimeUnit.SECONDS);
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
        if (subType == EnumSubType.DEFAULT) {
            // 用户使用客户端 sub 方法，增加订阅周期任务
            ScheduledFuture<?> future = blockingScheduler.scheduleWithFixedDelay(() -> {
                try {
                    log.trace("wconfig client scheduled sub is executing, namespace: {}, latestVersion: {}", namespace, latestVersion);
                    subscribeToAgent(namespace, latestModifiedTimestamp, latestVersion, null, localFile, fileType, EnumSubType.SCHEDULE);
                } catch (Throwable t) {
                    log.error("wconfig client sub schedule error", t);
                }
            }, 5, 5, TimeUnit.SECONDS);

            cacheSubSchedule(namespace, future, subType, SubscriptionContext.builder()
                    .callBackSet(new HashSet<>(Arrays.asList(callback)))
                    .localFile(localFile)
                    .fileType(fileType)
                    .build());
        }

        ConfigResponse localResponse = leaseConfigCache(namespace, localFile, fileType);

        ConfigResponseProcessor.processLocalConfig(namespace, localResponse, clientConfig.getEnvironment());

        return localResponse;
    }

    /**
     *
     */
    @Override
    public void unSubscribeToAgent(@NonNull String namespaceName) throws WConfigClientException {
        cancelSubSchedule(namespaceName);
    }

    @Override
    public List<String> getDirFromAgent(@NonNull String dirPath) throws WConfigClientException {
        // fixme
        return new ArrayList<>();
    }

    private ConfigResponse leaseConfigCache(String namespace, String localFile, EnumWConfigFileType fileType) throws WConfigClientException {
        ConfigResponse cachedResponse = configCache.getIfPresent(namespace);
        if (Objects.isNull(cachedResponse)) {
            // 未命中，从本地备份文件读取
            String locator = getClusterName() + CONFIG_LOCATOR_DELIMITER + getGroupName() + CONFIG_LOCATOR_DELIMITER + namespace;
            log.trace("wconfig client read local file {} of type {} for {}", localFile, fileType, locator);
            cachedResponse = ConfigResponse.builder()
                    .tripleLocator(locator)
                    .version(-1)
                    .config(LocalFileUtil.readFile(fileType, localFile))
                    .build();
        }
        configCache.put(namespace, cachedResponse);
        log.trace("wconfig client config cache leased");
        return cachedResponse;
    }
}

