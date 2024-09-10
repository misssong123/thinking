package netty.wconfig.client;

import com.google.common.base.Strings;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.WConfigAgentClient;
import netty.wconfig.client.agentclient.handlers.ClientChannelInitializer;
import netty.wconfig.client.agentclient.handlers.ClientInboundHandler;
import netty.wconfig.client.agentclient.handlers.ClientPipeTailHandler;
import netty.wconfig.client.agentclient.handlers.codecs.ClientFrameEncoder;
import netty.wconfig.client.agentclient.processors.ConfigResponseProcessor;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.agentclient.session.AgentClient;
import netty.wconfig.client.agentclient.session.AgentClientLocal;
import netty.wconfig.client.configs.ClientConfig;
import netty.wconfig.client.constants.ClientConstants;
import netty.wconfig.client.enums.EnumSubType;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.enums.EnumWConfigRawFileType;
import netty.wconfig.client.exceptions.WConfigClientException;
import netty.wconfig.client.utils.EncryptUtil;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;

import static netty.wconfig.client.constants.ClientConstants.*;
import static netty.wconfig.client.enums.EnumWConfigFileType.PROPERTIES;

@Slf4j
@RequiredArgsConstructor
public class WConfigClient {
    private static String secret;

    private static String groupName;

    public static String localFileFolder = "";

    private static boolean localDevMode = false;

    private static String localDevCluster = "";

    private static boolean agentDevMode = false;

    private static String agentIp = "127.0.0.1";

    private static int requestTimeoutInMillis = 1000 * 4;

    private static final Pattern namespacePattern = Pattern.compile(NAMESPACE_NAME_PATTERN);

    private static final Pattern groupPattern = Pattern.compile(GROUP_NAME_PATTERN);

    private final WConfigAgentClient agentClient;

    // 可以去掉这个单例设计，只依靠加锁和异常保证 getinstance 调用一次，因为这边无法依赖 spring 框架，所以无法判断 spring 容器内是否已经有一个单例了，而我也无法阻止 spring 用户来调用这里的方法
    // 估计是不应该同时兼顾 spring 和常规 java 客户端，应该分开实现
    private static class Singleton {
        private static final WConfigClient INSTANCE = init();
    }

    /**
     * 获取客户端实例，建立 agent 连接
     * 仅供非 spring 项目使用
     * 对于 spring 项目，直接注入容器中的 WConfigClient 实例
     *
     * @param secret          集群 secret，默认从相对路径文件 {@link ClientConstants#SETTING_FILE_DEFAULT} 中读取 wconfig.client.secret
     * @param groupName       配置 group，默认从相对路径文件 {@link ClientConstants#SETTING_FILE_DEFAULT} 中读取 wconfig.client.group
     * @param localFileFolder 本地备份路径，默认备份于相对路径 {@link ClientConstants#LOCAL_FOLDER_DEFAULT}
     * @param localDevMode    本地开发模式，默认 false
     * @param localDevCluster 本地开发模式的集群名称，需要与本地文件路径中的集群名一致
     * @param agentDevMode    连接 offline 环境开发专用 agent，默认 false，本地开发模式 localDevMode=true 时，此配置无效
     * @return WConfigClient 实例
     */
    public synchronized static WConfigClient getInstance(@NonNull String secret,
                                                         @Nullable String groupName,
                                                         @Nullable String localFileFolder,
                                                         boolean localDevMode,
                                                         @Nullable String localDevCluster,
                                                         boolean agentDevMode) throws WConfigClientException {
//        if(StringUtils.isNotBlank(groupName) && !groupPattern.matcher(groupName).matches()) {
//            log.error("invalid group name: {}, valid pattern: {}", groupName, groupPattern.pattern());
//            return null;
//        }

        if (!Strings.isNullOrEmpty(WConfigClient.secret)) {
            throw new WConfigClientException(String.format("wconfig client is already initialized. initialized-secret: %s, inputSecret: %s", WConfigClient.secret, secret));
        }

        WConfigClient.secret = secret.trim();
        WConfigClient.groupName = Strings.isNullOrEmpty(groupName) ? GROUP_NAME_DEFAULT : groupName.trim();
        WConfigClient.localFileFolder = Objects.isNull(localFileFolder) ? "" : localFileFolder.trim();
        WConfigClient.localDevMode = localDevMode;
        WConfigClient.localDevCluster = localDevCluster;
        WConfigClient.agentDevMode = agentDevMode;

        return Singleton.INSTANCE;
    }

    /**
     * 获取客户端实例，建立 agent 连接
     * 仅供非 spring 项目使用
     * 对于 spring 项目，直接注入容器中的 WConfigClient 实例
     *
     * @param settingFile 客户端配置文件，默认从相对路径 {@link ClientConstants#SETTING_FILE_DEFAULT} 中读取
     *                    wconfig.client.secret, wconfig.client.group, wconfig.client.local-file-folder, wconfig.client.local-dev-mode
     *                    也可使用绝对路径
     * @return {@link WConfigClient} 实例
     */
    public synchronized static WConfigClient getInstance(@Nullable String settingFile) throws WConfigClientException {
        if (!Strings.isNullOrEmpty(secret)) {
            throw new WConfigClientException(String.format("wconfig client is already initialized. initialized-secret: %s", secret));
        }

        settingFile = Strings.isNullOrEmpty(settingFile) ? SETTING_FILE_DEFAULT : settingFile;
        Properties properties = readProperties(settingFile);
        secret = properties.getProperty("wconfig.client.secret");
        groupName = properties.getProperty("wconfig.client.group-name", GROUP_NAME_DEFAULT).trim();
        localFileFolder = properties.getProperty("wconfig.client.local-file-folder", "").trim();
        localDevMode = Boolean.parseBoolean(properties.getProperty("wconfig.client.local-dev-mode", "false"));
        agentDevMode = Boolean.parseBoolean(properties.getProperty("wconfig.client.agent-dev-mode", "false"));
        agentIp = properties.getProperty("wconfig.client.agent-ip", "127.0.0.1");
        requestTimeoutInMillis = Integer.parseInt(properties.getProperty("wconfig.client.request-timeout-in-millis", "4000"));

        return Singleton.INSTANCE;
    }

    /**
     * non-spring dev only
     */
    public static Properties readProperties(String path) throws WConfigClientException {
        Properties properties = new Properties();
        try (InputStream input = WConfigClient.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                try (Reader reader = new FileReader(path)) {
                    properties.load(reader);
                } catch (Exception ex) {
                    log.error("", ex);
                    throw new WConfigClientException("wconfig client setting file not found: " + path);
                }
            } else {
                properties.load(input);
            }
        } catch (Exception ex) {
            log.error("", ex);
            throw new WConfigClientException("wconfig client setting file not found: " + path);
        }

        return properties;
    }

    /**
     * non-spring dev only
     */
    private static WConfigClient init() {

        ClientConfig clientConfig = new ClientConfig(secret, groupName, localDevMode, localDevCluster, agentDevMode, agentIp, requestTimeoutInMillis);
        ClientInboundHandler inboundHandler = clientConfig.clientInboundHandler();
        ClientFrameEncoder encoder = clientConfig.clientFrameEncoder();
        ClientPipeTailHandler tailHandler = clientConfig.clientPipeTailHandler();
        ClientChannelInitializer initializer = clientConfig.clientChannelInitializer(inboundHandler, encoder, tailHandler);
        NioEventLoopGroup nioGroup = clientConfig.nioGroup();
        Bootstrap bootstrap = clientConfig.bootstrap(initializer, nioGroup);
        WConfigAgentClient agentClient;
        if (clientConfig.isLocalDevMode()) {
            agentClient = new AgentClientLocal(clientConfig);
        } else {
            agentClient = new AgentClient(
                    bootstrap, nioGroup, clientConfig.agentAddress(), clientConfig.agentAddress2nd(), clientConfig);
        }
        try {
            agentClient.afterPropertiesSet();
        } catch (Throwable t) {
            log.error("wconfig client init failed", t);
            return null;
        }

        return new WConfigClient(agentClient);
    }

    /**
     * 停止客户端实例，关闭 agent 连接
     * 仅供非 spring 项目使用
     * 对于 spring 项目，由对应 bean 生命周期自动维护
     */
    public void destroy() throws WConfigClientException {
        log.info("WConfig client is shutting down...");
        try {
            agentClient.destroy();
        } catch (Exception e) {
            throw new WConfigClientException("wconfig client destroy error", e);
        }
    }

    /**
     * 获取一个配置文件的全部内容，同步返回
     *
     * @param namespace 配置空间，默认 {@link ClientConstants#NAMESPACE_NAME_DEFAULT}
     * @param localFile 本地备份文件名，默认使用文件名 {namespace}.{fileType}
     * @param fileType  本地备份文件类型，默认使用 {@link EnumWConfigFileType#PROPERTIES} 文件类型
     * @param autoPojo  与订阅 namespace 相对应的对象，在新配置发布后，该对象中对应成员将被自动更新，相同 namespace 只能配置一个 pojo，
     *                  对于 spring 工程，可用 {@link WConfig} 标记一个类，并在他的单例获得更新，而无需在此提交实例;
     *                  此方法不能保证该回填对象的线程安全
     * @return 指定 namespace 下所有配置；没有 agent 连接且没有本地配置文件（localPath）或 namespace 包含无效字符时返回 null
     */
    @Nullable
    public synchronized Map<String, String> getConfig(@Nullable String namespace,
                                                      @Nullable String localFile,
                                                      @Nullable EnumWConfigFileType fileType,
                                                      @Nullable Object autoPojo) throws WConfigClientException {

        if(StringUtils.isNotBlank(namespace) && !namespacePattern.matcher(namespace).matches()) {
            log.error("invalid namespace name: {}, valid pattern: {}", namespace, namespacePattern.pattern());
            return null;
        }

        namespace = Strings.isNullOrEmpty(namespace) ? NAMESPACE_NAME_DEFAULT : namespace.trim();
        ConfigResponse response = agentClient.getConfigCache(namespace);
        if (Objects.nonNull(response)) {
            log.trace("wconfig client getConfig from cache, namespace {}", namespace);
            return response.getConfig();
        }

        ConfigResponseProcessor.addWConfigPojo(namespace, autoPojo);
        fileType = Objects.isNull(fileType) ? PROPERTIES : fileType;
        localFile = getLocalFilePath(namespace, localFile, fileType);
        log.info("get-config localFile：{}", localFile);

        return agentClient.subscribeToAgent(namespace, -1L, -1, null, localFile, fileType, EnumSubType.GET).getConfig();
    }

    /**
     * 获取一个配置文件的全部内容，同步返回
     * 该方法供非 key-value 类型文件使用
     *
     * @param namespace 配置空间，默认 {@link ClientConstants#NAMESPACE_NAME_DEFAULT}
     * @param localFile 本地备份文件名，默认使用文件名 namespace（无后缀）
     * @param rawType   本地备份文件类型，默认使用 {@link EnumWConfigRawFileType#RAW} 文件类型
     * @return 指定 namespace 的完整文件文本，没有 agent 连接且没有本地配置文件（localPath）时返回 null
     */
    @Nullable
    public String getRawConfig(@Nullable String namespace,
                               @Nullable String localFile,
                               @Nullable EnumWConfigRawFileType rawType) throws WConfigClientException {
        rawType = Objects.isNull(rawType) ? EnumWConfigRawFileType.RAW : rawType;
        Map<String, String> config = getConfig(namespace, localFile, EnumWConfigFileType.fromValue(rawType.getValue()), null);
        return Objects.isNull(config) ? null : config.get(ITEM_KEY_DEFAULT);
    }

    /**
     * 订阅一个配置文件的全部内容，同步返回，自动更新 {@link WConfig} 标注的类的实例成员，无订阅回调，
     * 本地文件默认备份于相对路径 {@link ClientConstants#LOCAL_FOLDER_DEFAULT} 下的文件 {namespace}.{fileType}
     *
     * @param namespace 配置空间，默认 {@link ClientConstants#NAMESPACE_NAME_DEFAULT}
     * @return 指定 namespace 下所有配置，没有 agent 连接且没有本地配置文件（localPath）时返回 null
     */
    @Nullable
    public Map<String, String> subscribeConfig(@Nullable String namespace) throws WConfigClientException {
        return subscribeConfig(namespace, null, null, null, null);
    }

    /**
     * 订阅一个配置文件的全部内容，同步返回，自动更新 {@link WConfig} 标注的类的实例成员（或者在 autoPojo 提供的对象），
     * 执行订阅回调，在本地文件系统进行备份
     *
     * @param namespace 配置空间，默认 {@link ClientConstants#NAMESPACE_NAME_DEFAULT}
     * @param localFile 本地备份文件，默认使用文件名 {namespace}.{fileType}
     * @param fileType  本地备份文件类型，默认使用 {@link EnumWConfigFileType#PROPERTIES} 文件类型
     * @param autoPojo  与订阅 namespace 相对应的对象，在新配置发布后，该对象中对应成员将被自动更新，相同 namespace 只能配置一个 pojo，
     *                  对于 spring 工程，可用 {@link WConfig} 标记一个类，并在他的单例获得更新，而无需在此提交实例；
     *                  此方法不能保证该回填对象的线程安全
     * @param callback  订阅回调，在配置内容发生变化后被调用，
     *                  对于 spring 工程，可由 {@link WConfig} 标记的类去实现 {@link WConfigCallback} 接口，无需在此提交回调逻辑
     * @return 指定 namespace 下所有配置，没有 agent 连接且没有本地配置文件（localPath）或 namespace 包含无效字符时返回 null
     */
    @Nullable
    public synchronized Map<String, String> subscribeConfig(@Nullable String namespace,
                                                            @Nullable String localFile,
                                                            @Nullable EnumWConfigFileType fileType,
                                                            @Nullable Object autoPojo,
                                                            @Nullable WConfigCallback callback) throws WConfigClientException {

        if(StringUtils.isNotBlank(namespace) && !namespacePattern.matcher(namespace).matches()) {
            log.error("invalid namespace name: {}, valid pattern: {}", namespace, namespacePattern.pattern());
            return null;
        }

        namespace = Strings.isNullOrEmpty(namespace) ? NAMESPACE_NAME_DEFAULT : namespace.trim();
        fileType = Objects.isNull(fileType) ? PROPERTIES : fileType;
        localFile = getLocalFilePath(namespace, localFile, fileType);
        callback = Objects.isNull(callback) ? CALL_BACK_DEFAULT : callback;

        log.info("sub-config localFile：{}", localFile);

        if (agentClient.isSubscribed(namespace)) {
            log.trace("wconfig client sub get response from cache, namespace: {}", namespace);
            return getConfig(namespace, localFile, fileType, autoPojo);
        }

        ConfigResponseProcessor.addWConfigPojo(namespace, autoPojo);
        ConfigResponse cachedResponse = Optional.ofNullable(agentClient.getConfigCache(namespace))
                .orElseGet(() -> ConfigResponse.builder().version(-1).build());
        ConfigResponse newResponse = agentClient.subscribeToAgent(namespace, cachedResponse.getLatestModifiedTimestamp(), cachedResponse.getVersion(), callback, localFile, fileType, EnumSubType.DEFAULT);
        if (Objects.isNull(newResponse)) {
            throw new WConfigClientException("wconfig client null sub-response error");
        }

        return newResponse.getConfig();
    }

    /**
     * 订阅一个配置文件的全部内容，同步返回，自动更新 {@link WConfig} 标注的类的实例成员，执行订阅回调，在本地文件系统进行备份
     * 该方法供非 key-value 类型文件使用
     *
     * 默认 {@link ClientConstants#NAMESPACE_NAME_DEFAULT}
     * tants#NAMESPACE_NAME_DEFAULT}
     * @param localFile 本地备份文件，默认使用文件 {namespace}.{fileType}
     * @param rawType   本地备份文件类型，非 key-value 类型文件
     * @param callback  订阅回调，在配置内容发生变化后被调用，提供 groupName, namespace 和新版本配置文件的完整内容
     * @return 指定 namespace 下所有配置, 没有 agent 连接且没有本地配置文件（localPath）时返回 null
     */
    @Nullable
    public String subscribeRawConfig(@Nullable String namespace,
                                     @Nullable String localFile,
                                     @Nullable EnumWConfigRawFileType rawType,
                                     @Nullable WConfigCallback callback) throws WConfigClientException {
        rawType = Objects.isNull(rawType) ? EnumWConfigRawFileType.RAW : rawType;
        Map<String, String> config = subscribeConfig(namespace, localFile, EnumWConfigFileType.fromValue(rawType.getValue()), null, callback);
        return Objects.nonNull(config) ? config.get(ITEM_KEY_DEFAULT) : null;
    }

    /**
     *
     */
    public void unSubscribeConfig(@Nullable String namespaceName) throws WConfigClientException {
        namespaceName = Strings.isNullOrEmpty(namespaceName) ? NAMESPACE_NAME_DEFAULT : namespaceName.trim();
        agentClient.unSubscribeToAgent(namespaceName);
    }

    /**
     *
     */
    public List<String> getDirectory(@NonNull String dirPath) throws WConfigClientException {
        return agentClient.getDirFromAgent(dirPath);
    }

    private String getLocalFilePath(String namespace, String localFile, EnumWConfigFileType fileType) {
        String localFileFolder = WConfigClient.localFileFolder;
        if (Strings.isNullOrEmpty(localFileFolder)) {
            localFileFolder = LOCAL_FOLDER_DEFAULT;
        } else if (!localFileFolder.endsWith("/")) {
            localFileFolder += "/";
        }

        localFileFolder += EncryptUtil.encrypt(agentClient.getSecret()) + "/";
        localFileFolder += agentClient.getGroupName() + "/";
        String typeSuffix = fileType == EnumWConfigFileType.RAW ? fileType.getValue() : "." + fileType.getValue();
        return Strings.isNullOrEmpty(localFile)
                ? localFileFolder + namespace.replace("/", "~") + typeSuffix
                : localFileFolder + localFile.trim();
    }
}
