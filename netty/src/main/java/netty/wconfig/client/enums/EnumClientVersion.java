package netty.wconfig.client.enums;

public enum EnumClientVersion {
    V1((byte) 1),
    V2((byte) 2),
    V3((byte) 3);

    private final byte value;

    EnumClientVersion(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
