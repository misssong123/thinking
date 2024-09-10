package netty.wconfig.client.agentclient.handlers;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ClientPipeTailHandler extends ChannelDuplexHandler {

    /*
     * inbound exception handler
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("wconfig-client inbound handler error, agent: {}", ctx.channel().remoteAddress(), cause);
    }

    /*
     * outbound logger & exception listener
     * */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            log.debug("sending request to agent: {}, msg: {}", ctx.channel().remoteAddress(), msg);
            if (!future.isSuccess()) {
                Throwable cause = future.cause();
                log.error("agent-client outbound handler error, agent: {}", ctx.channel().remoteAddress(), cause);
            }
        }));
    }
}
