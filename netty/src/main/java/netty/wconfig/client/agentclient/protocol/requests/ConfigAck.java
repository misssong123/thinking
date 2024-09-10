package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import java.nio.charset.StandardCharsets;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Builder
public class ConfigAck extends BaseRequest {

    private final String clusterName;
    private final String groupName;
    private final String namespaceName;
    private final Integer latestVersion;

    @Override
    public int encode(ByteBuf out) throws Exception {
        int start = out.writerIndex();
        out
                .writeInt(clusterName.length())
                .writeBytes(clusterName.getBytes(StandardCharsets.UTF_8))
                .writeInt(groupName.length())
                .writeBytes(groupName.getBytes(StandardCharsets.UTF_8))
                .writeInt(namespaceName.length())
                .writeBytes(namespaceName.getBytes(StandardCharsets.UTF_8))
                .writeInt(latestVersion);

        return out.writerIndex() - start;
    }

    @Override
    public EnumClientMessageType getType() {
        return EnumClientMessageType.CONF_ACK;
    }

    public void send(Channel chl) {
        chl.writeAndFlush(this.toFrame());
    }
}

