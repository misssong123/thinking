package netty.wconfig.client.configs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import netty.wconfig.client.WConfigClient;
import netty.wconfig.client.agentclient.WConfigAgentClient;
import netty.wconfig.client.agentclient.handlers.ClientChannelInitializer;
import netty.wconfig.client.agentclient.handlers.ClientInboundHandler;
import netty.wconfig.client.agentclient.handlers.ClientPipeTailHandler;
import netty.wconfig.client.agentclient.handlers.codecs.ClientFrameEncoder;
import netty.wconfig.client.agentclient.processors.WConfigAnnotationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.net.InetSocketAddress;
import netty.wconfig.client.agentclient.session.AgentClientLocal;
import netty.wconfig.client.agentclient.session.AgentClient;
import static io.netty.channel.ChannelOption.*;
import static io.netty.channel.ChannelOption.SO_SNDBUF;
import static netty.wconfig.client.constants.ClientConstants.*;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "wconfig.client")
@ConditionalOnProperty(name = "wconfig.client.secret")
@Configuration
public class ClientConfig {

    @Autowired
    private Environment environment;

    private String agentIp = "127.0.0.1";

    private int agentPort = 6530;

    private int agentPort2nd = 36530;

    //    @Value("${wconfig.client.secret}")
    private String secret;

    //    @Value("${wconfig.client.group-name}")
    private String groupName = GROUP_NAME_DEFAULT;

    //    @Value("${wconfig.client.local-file-folder}")
    private String localFileFolder = LOCAL_FOLDER_DEFAULT;

    //    @Value("${wconfig.client.local-dev-mode}")
    private boolean localDevMode = false;

    private String localDevCluster = "";

    private boolean agentDevMode = false;

    private boolean keepAlive = true;

    private int connectTimeOutInMillis = 1000 * 10;

    private int requestTimeoutInMillis = 1000 * 4;

    private boolean noDelay = true;

    private int maxFrameLength = 1024 * 1024 * 10;

    private int rcvBuf = 1024 * 1024 * 10;

    private int sndBuf = 1024 * 1024 * 2;

    public ClientConfig(String secret,
                        String groupName,
                        Boolean localDevMode,
                        String localDevCluster,
                        Boolean agentDevMode,
                        String agentIp,
                        int requestTimeoutInMillis) {
        this.secret = secret;
        this.groupName = groupName;
        this.localDevMode = localDevMode;
        this.localDevCluster = localDevCluster;
        this.agentDevMode = agentDevMode;
        this.agentIp = agentIp;
        this.requestTimeoutInMillis = requestTimeoutInMillis;
    }

    @Bean
    public ClientInboundHandler clientInboundHandler() {
        return new ClientInboundHandler();
    }

    @Bean
    public ClientFrameEncoder clientFrameEncoder() {
        return new ClientFrameEncoder();
    }

    @Bean
    public ClientPipeTailHandler clientPipeTailHandler() {
        return new ClientPipeTailHandler();
    }

    @Bean
    public ClientChannelInitializer clientChannelInitializer(ClientInboundHandler inboundHandler,
                                                             ClientFrameEncoder encoder,
                                                             ClientPipeTailHandler tailHandler) {
        return new ClientChannelInitializer(inboundHandler, encoder, tailHandler);
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup nioGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public Bootstrap bootstrap(ClientChannelInitializer channelInitializer,
                               NioEventLoopGroup nioGroup) {

        channelInitializer.setMaxFrameLength(maxFrameLength);
        return new Bootstrap()
                .group(nioGroup)
                .channel(NioSocketChannel.class)
                .option(SO_KEEPALIVE, keepAlive)
                .option(TCP_NODELAY, noDelay)
                .option(SO_RCVBUF, rcvBuf)
                .option(SO_SNDBUF, sndBuf)
                .handler(channelInitializer);
    }

    @Bean
    public InetSocketAddress agentAddress() {
        return newSocket(agentPort);
    }

    @Bean
    public InetSocketAddress agentAddress2nd() {
        return newSocket(agentPort2nd);
    }

    private InetSocketAddress newSocket(int port) {
        if (System.getenv().containsKey("HOST_IP")) {
            agentIp = System.getenv().get("HOST_IP");
        }

        if (!localDevMode && agentDevMode) {
            agentIp = DEV_AGENT_HOST;
        }

        return new InetSocketAddress(agentIp, port);
    }

    @ConditionalOnProperty(name = "wconfig.client.local-dev-mode", havingValue = "true")
    @Bean
    public AgentClientLocal agentClientLocal(ClientConfig clientConfig) {
        return new AgentClientLocal(clientConfig);
    }

    @ConditionalOnMissingBean(name = "agentClientLocal")
    @Bean
    public AgentClient agentClient(Bootstrap bootstrap,
                                   NioEventLoopGroup nioGroup,
                                   InetSocketAddress agentAddress,
                                   InetSocketAddress agentAddress2nd,
                                   ClientConfig clientConfig) {
        return new AgentClient(bootstrap, nioGroup, agentAddress, agentAddress2nd, clientConfig);
    }

    @Bean
    public WConfigClient wConfigClient(WConfigAgentClient wConfigAgentClient) {
        WConfigClient.localFileFolder = this.localFileFolder;
        return new WConfigClient(wConfigAgentClient);
    }

    @Bean
    public WConfigAnnotationProcessor wConfigAnnotationProcessor(WConfigClient wConfigClient) {
        return new WConfigAnnotationProcessor(wConfigClient, environment);
    }
}
