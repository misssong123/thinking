package netty.wconfig.client.agentclient.protocol.responses;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import static java.nio.charset.StandardCharsets.UTF_8;

@ToString
@Builder
@Getter
public class CommonResponse extends BaseResponse {

    private int code;
    private String value;

    @Override
    public BaseResponse decode(ByteBuf in) throws Exception {
        code = in.readInt();
        int valueLength = in.readInt();
        value = in.readCharSequence(valueLength, UTF_8).toString();
        return this;
    }

    @Override
    public EnumClientMessageType getType() {
        return EnumClientMessageType.COMMON_RESP;
    }
}
