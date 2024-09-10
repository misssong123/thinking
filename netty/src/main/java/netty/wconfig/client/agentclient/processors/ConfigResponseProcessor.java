package netty.wconfig.client.agentclient.processors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.WConfigField;
import netty.wconfig.client.agentclient.SubscriptionContext;
import netty.wconfig.client.agentclient.WConfigAgentClient;
import netty.wconfig.client.agentclient.protocol.requests.ConfigAck;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.exceptions.WConfigClientException;
import netty.wconfig.client.utils.LocalFileUtil;
import netty.wconfig.client.utils.ValueUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class ConfigResponseProcessor {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("WConfig_Client_Sub_Callback_Thread_%d").setDaemon(true).build()
    );

    private static final Map<String, Object> autoPopulatedConfigPojoMap = new ConcurrentHashMap<>();

    @Setter
    private static WConfigAgentClient agentClient;

    public static void addWConfigPojo(String namespace, Object autoPojo) {
        if (Objects.nonNull(autoPojo)) {
            autoPopulatedConfigPojoMap.put(namespace, autoPojo);
        }
    }

    public static void processLocalConfig(String namespace,
                                          ConfigResponse localConfig,
                                          Environment environment) {
        Object autoPojo = autoPopulatedConfigPojoMap.get(namespace);
        if (Objects.nonNull(autoPojo)) {
            // 有自动回填配置的 namespace，更新数据
            try {
                Map<String, String> newConfigMap = localConfig.getConfig();
                // 先按类字段名称复制一遍
                BeanUtils.populate(autoPojo, newConfigMap);
                // 再根据 WConfigField 注解名称补充复制一遍
                try {
                    populateWithSpring(autoPojo, newConfigMap, environment);
                } catch (Throwable t) {
                    if (t.getCause() instanceof NoClassDefFoundError) {
                        // 非 spring 应用
                        populateWithNonSpring(autoPojo, newConfigMap, environment);
                    } else {
                        log.error("wconfig client autoPojo error", t);
                    }
                }

                log.debug("wconfig client autoPojo populated, namespace {}, local-config: {}", namespace, localConfig);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("wconfig client autoPojo populating error", e);
            }
        }

        SubscriptionContext context = agentClient.getSubContext(namespace);
        if (Objects.nonNull(context) && Objects.nonNull(context.getCallBackSet())) {
            // 当前 namespace 有订阅（sub）记录，执行回调
            executor.submit(() -> context.getCallBackSet().forEach(callback -> {
                // 该 callback 有可能由 get 请求第一次调用（sub）产生，此时为 null，缓存过期后 un-sub 时会被清理
                Optional.ofNullable(callback).ifPresent(nonNullCallback -> {
                    try {
                        nonNullCallback.callback(namespace, localConfig.getConfig());
                    } catch (Throwable t) {
                        log.error("wconfig client callback error, namespace: {}", namespace, t);
                    }
                });
                log.debug("wconfig client callback executed, namespace: {}, newConfig: {}", namespace, localConfig);
            }));
        } else {
            log.trace("wconfig client callback not registered. context：{}", context);
        }
    }

    /**
     *
     */
    public static void processNewConfig(String cluster,
                                        String group,
                                        String namespace,
                                        ConfigResponse newConfig,
                                        EnumWConfigFileType fileType,
                                        String localFile,
                                        Channel channel,
                                        Environment environment) throws WConfigClientException {
        if (!agentClient.putConfigCache(namespace, newConfig)) {
            // 配置版本没有变化（没有新变更时间戳，版本仍相等），不进行后续处理
            return;
        }

        Object autoPojo = autoPopulatedConfigPojoMap.get(namespace);
        if (Objects.nonNull(autoPojo)) {
            // 有自动回填配置的 namespace，更新数据
            try {
                Map<String, String> newConfigMap = newConfig.getConfig();
                // 先按类字段名称复制一遍
                BeanUtils.populate(autoPojo, newConfigMap);
                // 再根据 WConfigField 注解名称补充复制一遍
                try {
                    populateWithSpring(autoPojo, newConfigMap, environment);
                } catch (Throwable t) {
                    if (t.getCause() instanceof NoClassDefFoundError) {
                        // 非 spring 应用
                        populateWithNonSpring(autoPojo, newConfigMap, environment);
                    } else {
                        log.error("wconfig client autoPojo error", t);
                    }
                }

                log.debug("wconfig client autoPojo populated, namespace {}, newConfig: {}", namespace, newConfig);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("wconfig client autoPojo populating error", e);
            }
        }

        SubscriptionContext context = agentClient.getSubContext(namespace);
        if (Objects.nonNull(context) && Objects.nonNull(context.getCallBackSet())) {
            // 当前 namespace 有订阅（sub）记录，执行回调
            executor.submit(() -> context.getCallBackSet().forEach(callback -> {
                // 该 callback 有可能由 get 请求第一次调用（sub）产生，此时为 null，缓存过期后 un-sub 时会被清理
                Optional.ofNullable(callback).ifPresent(nonNullCallback -> {
                    try {
                        nonNullCallback.callback(namespace, newConfig.getConfig());
                    } catch (Throwable t) {
                        log.error("wconfig client callback error, namespace: {}", namespace, t);
                    }
                });
                log.debug("wconfig client callback executed, namespace: {}, newConfig: {}", namespace, newConfig);
            }));
        } else {
            log.trace("wconfig client callback not registered. context：{}", context);
        }

        // 写入本地备份文件
        if (Objects.isNull(fileType)) {
            SubscriptionContext subCtx = Optional
                    .ofNullable(agentClient.getSubContext(namespace))
                    .orElseThrow(() -> new WConfigClientException("subscription context not found for namespace " + namespace));
            fileType = subCtx.getFileType();
            localFile = subCtx.getLocalFile();
        }
        LocalFileUtil.writeFile(fileType, localFile, newConfig.getConfig());

        // 向 agent 回报当前使用版本
        ConfigAck.builder()
                .clusterName(cluster)
                .groupName(group)
                .namespaceName(namespace)
                .latestVersion(newConfig.getVersion())
                .build()
                .send(channel);
    }

    private static void populateWithSpring(Object autoPojo, Map<String, String> newConfigMap, Environment environment) {
        ReflectionUtils.doWithFields(autoPojo.getClass(), field -> {
            String fieldAlia = field.getAnnotation(WConfigField.class).value();
            fieldAlia = ValueUtil.resolveEmbeddedValue(fieldAlia, environment, true);
            log.info("spring fieldAlia:{}", fieldAlia);
            String value = newConfigMap.get(fieldAlia);
            field.setAccessible(true);
            field.set(autoPojo, value);
        }, field -> field.isAnnotationPresent(WConfigField.class));
    }

    private static void populateWithNonSpring(Object autoPojo, Map<String, String> newConfigMap, Environment environment) throws IllegalAccessException {
        List<Field> fields = Arrays.stream(autoPojo.getClass().getDeclaredFields()).filter(field ->
                field.isAnnotationPresent(WConfigField.class)
        ).collect(Collectors.toList());
        for (Field field : fields) {
            String fieldAlia = field.getAnnotation(WConfigField.class).value();
            fieldAlia = ValueUtil.resolveEmbeddedValue(fieldAlia, environment, false);
            log.info("nonSpring fieldAlia:{}", fieldAlia);
            String value = newConfigMap.get(fieldAlia);
            field.setAccessible(true);
            field.set(autoPojo, value);
        }
    }
}
