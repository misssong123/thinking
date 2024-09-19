package netty.wconfig.client.agentclient.handlers.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.agentclient.protocol.requests.BaseRequest;

import static netty.wconfig.client.constants.ClientConstants.P_END_TAG;

@Slf4j
@ChannelHandler.Sharable
public class ClientFrameEncoder extends MessageToByteEncoder<ClientFrame> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientFrame frame, ByteBuf out) throws Exception {
        BaseRequest request = (BaseRequest) frame.getMessage();
        out.writeByte(frame.getType())
                .writeByte(frame.getVersion())
                .writeInt(frame.getRequestId());
        int mark = out.writerIndex();
        out.writeInt(0);
        int length = request.encode(out);
        out.writeBytes(P_END_TAG)
                .markWriterIndex()
                .writerIndex(mark)
                .writeInt(length)
                .resetWriterIndex();
    }
}
