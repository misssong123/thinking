package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import java.nio.charset.StandardCharsets;

@ToString
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UnSubRequest extends BaseRequest {

    private final String clusterName;
    private final String groupName;     /*主版本分组名*/
    private final String namespaceName;

    @Override
    public int encode(ByteBuf out) throws Exception {
        int start = out.writerIndex();
        out
                .writeInt(clusterName.length())
                .writeBytes(clusterName.getBytes(StandardCharsets.UTF_8))
                .writeInt(groupName.length())
                .writeBytes(groupName.getBytes(StandardCharsets.UTF_8))
                .writeInt(namespaceName.length())
                .writeBytes(namespaceName.getBytes(StandardCharsets.UTF_8));
        return out.writerIndex() - start;
    }

    @Override
    public EnumClientMessageType getType() {
        return EnumClientMessageType.CONF_UN_SUB_REQ;
    }
}

