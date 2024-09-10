package netty.wconfig.client.agentclient.protocol.responses;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import netty.wconfig.client.enums.EnumClientMessageType;
import netty.wconfig.client.utils.JsonUtil;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import netty.wconfig.client.enums.EnumWConfigFileType;
import static netty.wconfig.client.constants.ClientConstants.ITEM_KEY_DEFAULT;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ConfigResponse extends BaseResponse {

    private String tripleLocator;
    private EnumWConfigFileType fileType;
    private int version;
    private long latestModifiedTimestamp;
    private Map<String, String> config;

    @Override
    public BaseResponse decode(ByteBuf in) throws Exception {
        int tripleLocatorLen = in.readInt();
        this.tripleLocator = in.readCharSequence(tripleLocatorLen, StandardCharsets.UTF_8).toString();
        int fileTypeLen = in.readInt();
        this.fileType = EnumWConfigFileType.fromValue(in.readCharSequence(fileTypeLen, StandardCharsets.UTF_8).toString());
        this.latestModifiedTimestamp = in.readLong();
        this.version = in.readInt();
        int configLen = in.readInt();
        switch (fileType) {
            case YML:
            case PROPERTIES:
                this.config = JsonUtil.parse(in.readCharSequence(configLen, StandardCharsets.UTF_8).toString(), new TypeReference<Map<String, String>>() {});
                break;
            case XML:
            case JSON:
            case TXT:
            case RAW:
                this.config = new HashMap<>();
                this.config.put(ITEM_KEY_DEFAULT, in.readCharSequence(configLen, StandardCharsets.UTF_8).toString());
                break;
            default:
                break;
        }
        return this;
    }

    @Override
    public EnumClientMessageType getType() {
        return EnumClientMessageType.CONF_RESP;
    }
}

