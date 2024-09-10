package netty.wconfig.client.agentclient.protocol.responses;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import netty.wconfig.client.enums.EnumClientMessageType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static netty.wconfig.client.enums.EnumClientMessageType.DIR_RESP;

@ToString
@Builder
@Getter
public class DirResponse  extends BaseResponse {
    private String dirPath;
    private int childrenNum;
    private List<String> childrenPaths;

    @Override
    public BaseResponse decode(ByteBuf in) throws Exception {
        this.childrenNum = in.readInt();
        int dirPathLen = in.readInt();
        this.dirPath = in.readCharSequence(dirPathLen, StandardCharsets.UTF_8).toString();
        List<String> childrenPaths = new ArrayList<>();
        while (in.readableBytes() > 0) {
            int childrenPathLen = in.readInt();
            String childrenPath = in.readCharSequence(childrenPathLen, StandardCharsets.UTF_8).toString();
            childrenPaths.add(childrenPath);
        }
        if (childrenNum != childrenPaths.size()) {
            throw new Exception("childrenNum and childrenPaths not match for conversion to DirResponse.");
        }
        this.childrenPaths = childrenPaths;
        return this;
    }

    @Override
    public EnumClientMessageType getType() {
        return DIR_RESP;
    }
}