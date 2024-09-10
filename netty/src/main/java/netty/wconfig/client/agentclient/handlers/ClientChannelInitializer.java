package netty.wconfig.client.agentclient.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import netty.wconfig.client.agentclient.handlers.codecs.ClientFrameDecoder;
import netty.wconfig.client.agentclient.handlers.codecs.ClientFrameEncoder;

import static netty.wconfig.client.constants.ClientConstants.*;

@RequiredArgsConstructor
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ClientInboundHandler inboundHandler;

    private final ClientFrameEncoder encoder;

    private final ClientPipeTailHandler tailHandler;

    @Setter
    private int maxFrameLength;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, HEAD_WO_MSG_LEN, INT_LEN, P_END_TAG.length, 0, true))
                .addLast(new ClientFrameDecoder())
                .addLast(inboundHandler)
                .addLast(encoder)
                .addLast(tailHandler);
    }
}
