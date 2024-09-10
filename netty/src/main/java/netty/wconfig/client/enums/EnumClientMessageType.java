package netty.wconfig.client.enums;

public enum EnumClientMessageType {
    CONF_SUB_REQ((byte) 2),         //订阅配置或拉取配置
    CONF_UN_SUB_REQ((byte) 3),      //取消订阅
    CONF_RESP((byte) 4),
    CONF_ACK((byte) 18),        //上报使用的配置版本
    PING_REQ((byte) 5),
    PING_RESP((byte) 6),
    DIR_GET_REQ((byte) 7),
    DIR_RESP((byte) 8),
    AUTH_REQ((byte) 16),        //get authority request
    COMMON_RESP((byte) 12),       // FIXME protocol type num
    INVALID((byte) 99);

    private final byte value;

    EnumClientMessageType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static EnumClientMessageType fromByte(byte intValue) throws Exception {
        switch (intValue) {
//            case 1:
//                return CONF_GET_REQ;
            case 2:
                return CONF_SUB_REQ;
            case 3:
                return CONF_UN_SUB_REQ;
            case 4:
                return CONF_RESP;
            case 18:
                return CONF_ACK;
            case 5:
                return PING_REQ;
            case 6:
                return PING_RESP;
            case 7:
                return DIR_GET_REQ;
            case 8:
                return DIR_RESP;
            case 16:
                return AUTH_REQ;
            case 12:
                return COMMON_RESP;
            default:
                return INVALID;
        }
    }
}
