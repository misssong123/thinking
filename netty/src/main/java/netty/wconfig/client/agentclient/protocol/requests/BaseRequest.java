package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.constants.ClientConstants;
import netty.wconfig.client.enums.EnumClientMessageType;

public abstract class BaseRequest {

    /**
     * @return written length in bytes
     * */
    public abstract int encode(ByteBuf out) throws Exception;

    public abstract EnumClientMessageType getType();

    public ClientFrame toFrame(int requestId) {
        return ClientFrame.builder()
                .type(getType().getValue())
                .version(ClientConstants.PROTOCOL_VERSION)
                .requestId(requestId)
                .message(this)
                .build();
    }

    public ClientFrame toFrame() {
        return ClientFrame.builder()
                .type(getType().getValue())
                .version(ClientConstants.PROTOCOL_VERSION)
                .message(this)
                .build();
    }
}
