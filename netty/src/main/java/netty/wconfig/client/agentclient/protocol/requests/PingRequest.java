package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import static netty.wconfig.client.enums.EnumClientMessageType.PING_REQ;

@ToString
@Builder
public class PingRequest extends BaseRequest {

    @Override
    public int encode(ByteBuf out) {
        return 0;
    }

    @Override
    public EnumClientMessageType getType() {
        return PING_REQ;
    }
}
