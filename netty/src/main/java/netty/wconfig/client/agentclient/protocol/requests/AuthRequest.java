package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;
import netty.wconfig.client.utils.JsonUtil;

import java.nio.charset.StandardCharsets;

import static netty.wconfig.client.enums.EnumClientMessageType.AUTH_REQ;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Builder
public class AuthRequest extends BaseRequest {

    private final String secret;
    private final String sdkVersion;

    @Override
    public int encode(ByteBuf out) throws Exception {
        int start = out.writerIndex();
        String json = JsonUtil.toString(this);
        out.writeInt(json.length());
        out.writeBytes(json.getBytes(StandardCharsets.UTF_8));
        return out.writerIndex() - start;
    }

    @Override
    public EnumClientMessageType getType() {
        return AUTH_REQ;
    }
}
