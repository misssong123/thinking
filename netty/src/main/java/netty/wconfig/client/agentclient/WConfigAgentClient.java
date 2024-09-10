package netty.wconfig.client.agentclient;

import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import netty.wconfig.client.WConfigCallback;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.enums.EnumSubType;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.exceptions.WConfigClientException;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

public interface WConfigAgentClient {
    ConfigResponse getConfigCache(@NonNull String namespace);

    boolean putConfigCache(@NonNull String namespace, @NonNull ConfigResponse configResponse);

    boolean isSubscribed(@NonNull String namespace);

    void cacheSubSchedule(@NonNull String namespace, @NonNull ScheduledFuture<?> future, @NonNull EnumSubType subType, @NonNull SubscriptionContext context);

    SubscriptionContext getSubContext(@NonNull String namespace);

    void cancelSubSchedule(@NonNull String namespace);

    String getClusterName();

    String getGroupName();

    String getSecret();

    void afterPropertiesSet() throws Exception;

    void destroy();

    boolean isDestroyed();

    ConfigResponse subscribeToAgent(@NonNull String namespace,
                                    @NonNull Long latestModifiedTimestamp,
                                    @NonNull Integer latestVersion,   // sub 之前版本
                                    @Nullable WConfigCallback callback,
                                    @NonNull String localFile,
                                    @NonNull EnumWConfigFileType fileType,
                                    @NonNull EnumSubType subType) throws WConfigClientException;

    void unSubscribeToAgent(@NonNull String namespaceName) throws WConfigClientException;

    List<String> getDirFromAgent(@NonNull String dirPath) throws WConfigClientException;
}
