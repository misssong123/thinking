package netty.wconfig.client.agentclient.protocol.requests;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import java.nio.charset.StandardCharsets;

import static netty.wconfig.client.enums.EnumClientMessageType.DIR_GET_REQ;

@Data
@ToString
@Builder
public class DirRequest extends BaseRequest {

    private final String clusterName;
    private final String dirPath;

    @Override
    public int encode(ByteBuf out) {
        int start = out.writerIndex();
        out
                .writeInt(clusterName.length())
                .writeBytes(clusterName.getBytes(StandardCharsets.UTF_8))
                .writeInt(dirPath.length())
                .writeBytes(dirPath.getBytes(StandardCharsets.UTF_8));
        return out.writerIndex() - start;
    }

    @Override
    public EnumClientMessageType getType() {
        return DIR_GET_REQ;
    }
}
