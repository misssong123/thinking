package netty.wconfig.client.agentclient.protocol.responses;

import io.netty.buffer.ByteBuf;
import netty.wconfig.client.enums.EnumClientMessageType;

public abstract class BaseResponse {

    public abstract BaseResponse decode(ByteBuf in) throws Exception;

    public abstract EnumClientMessageType getType();
}
