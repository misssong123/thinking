package netty.wconfig.client.agentclient.handlers.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.agentclient.protocol.responses.BaseResponse;
import netty.wconfig.client.agentclient.protocol.responses.ResponseFactory;
import netty.wconfig.client.exceptions.WConfigClientProtocolException;

import java.util.List;

import static netty.wconfig.client.constants.ClientConstants.HEAD_LEN;
import static netty.wconfig.client.constants.ClientConstants.P_END_TAG;

@Slf4j
public class ClientFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf stripped = null;
        try {
            /* frame length w/o delimiter */
            int frameLen = in.readableBytes() - P_END_TAG.length;
            stripped = in.readRetainedSlice(frameLen);
            in.skipBytes(P_END_TAG.length);

            ClientFrame frame = ClientFrame.builder()
                    /* header.type */
                    .type(stripped.readByte())
                    /* header.version */
                    .version(stripped.readByte())
                    /* header.trace */
                    .requestId(stripped.readInt()).build();

            /* body.size */
            int msgLen = stripped.readInt();
            if (frameLen != msgLen + HEAD_LEN) {
                throw new WConfigClientProtocolException("Invalid bytes for frame decoding. frameLen: " + frameLen + " msgLen: " + msgLen + " header: " + frame);
            }

            BaseResponse response;
            byte version = frame.getVersion();
            switch (version) {
                case 0:
                case 1:
                case 2:
                case 3:
                    response = ResponseFactory.get(frame.getType(), version);
                    break;
                default:
                    String errorMsg = "Unsupported agent response version: " + version;
                    log.error(errorMsg);
                    throw new WConfigClientProtocolException(errorMsg);
            }

            /* body */
            try {
                frame.setMessage(response.decode(stripped));
            } catch (Throwable t) {
                throw new WConfigClientProtocolException(String.format("Response body decode error type: %s version: %s ", frame.getType(), frame.getVersion()), t);
            }

            out.add(frame);

        } finally {
            if (stripped != null) {
                stripped.release();
            }
        }
    }
}
