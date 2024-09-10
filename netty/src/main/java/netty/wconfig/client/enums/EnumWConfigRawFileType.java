package netty.wconfig.client.enums;

public enum EnumWConfigRawFileType {
    RAW(""),
    TXT("txt"),
    XML("xml"),
    INVALID("");

    private final String value;

    EnumWConfigRawFileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EnumWConfigRawFileType fromValue(String value) {
        switch (value) {
            case "": return RAW;
            case "txt": return TXT;
            case "xml": return XML;
            default: return INVALID;
        }
    }
}
