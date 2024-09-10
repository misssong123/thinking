package netty.wconfig.client.agentclient.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.processors.ConfigResponseProcessor;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.agentclient.protocol.responses.ConfigResponse;
import netty.wconfig.client.agentclient.session.RequestSession;
import netty.wconfig.client.agentclient.session.RequestSessionTracker;
import netty.wconfig.client.enums.EnumClientMessageType;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.UUID;

import static netty.wconfig.client.constants.ClientConstants.CONFIG_LOCATOR_DELIMITER;
import static netty.wconfig.client.constants.MDCConstants.TRACE_AGENT;
import static netty.wconfig.client.constants.MDCConstants.TRACE_ID;

@Slf4j
@ChannelHandler.Sharable
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MDC.put(TRACE_ID, TRACE_AGENT + UUID.randomUUID());
        ClientFrame frame = (ClientFrame) msg;
        log.debug("received response from agent: {}, msg: {}", ctx.channel().remoteAddress(), frame);
        RequestSession session = RequestSessionTracker.getSession(frame.getRequestId());
        if (Objects.isNull(session)) {
            byte type = frame.getType();
            // 只被动接收 agent 主动推送配置
            if (EnumClientMessageType.fromByte(type) != EnumClientMessageType.CONF_RESP) {
                log.debug("ignored inbound frame, type {}, request id {}", type, frame.getRequestId());
                MDC.clear();
                return;
            }

            ConfigResponse configResponse = (ConfigResponse) frame.getMessage();
            String[] locator = configResponse.getTripleLocator().split(CONFIG_LOCATOR_DELIMITER);
            String cluster = locator[0];
            String group = locator[1];
            String namespace = locator[2];
            ConfigResponseProcessor.processNewConfig(
                    cluster, group, namespace, configResponse, null, null, ctx.channel(), null);
        } else {
            // 同步请求的响应
            session.setFrame(frame);
        }

        MDC.clear();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive {}", ctx.channel().remoteAddress());
    }
}
