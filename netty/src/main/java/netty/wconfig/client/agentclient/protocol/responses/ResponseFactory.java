package netty.wconfig.client.agentclient.protocol.responses;

import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.enums.EnumClientMessageType;

@Slf4j
public class ResponseFactory {

    /**
     * @return not null
     * */
    public static BaseResponse get(byte type, byte version) throws Exception {
        EnumClientMessageType enumType = EnumClientMessageType.fromByte(type);
        switch (enumType) {
            case CONF_RESP:
                return ConfigResponse.builder().build();
            case PING_RESP:
                return PingResponse.builder().build();
            case DIR_RESP:
                return DirResponse.builder().build();
            case COMMON_RESP:
                return CommonResponse.builder().build();
            default:
                throw new Exception("Unsupported response type: " + enumType.name() + " of version " + version);
        }
    }
}