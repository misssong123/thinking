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
        out
                /* header.type */
                .writeByte(frame.getType())
                /* header.version */
                .writeByte(frame.getVersion())
                /* header.trace */
                .writeInt(frame.getRequestId());

        // do NOT use out.markWriterIndex(), there's only one mark slot,
        // there could be another marker inside the following response.encode(out) code
        int mark = out.writerIndex();
        /* hold body.size slot with 0 */
        out.writeInt(0);
        /* body */
        int length = request.encode(out);
        out
                /* frame delimiter */
                .writeBytes(P_END_TAG)
                /* mark the tail */
                .markWriterIndex()
                /* move back to body.size slot */
                .writerIndex(mark)
                /* body.size */
                .writeInt(length)
                /* move writer to tail */
                .resetWriterIndex();
    }
}
