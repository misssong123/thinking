package netty.wconfig.client.enums;

public enum EnumWConfigFileType {
    PROPERTIES("properties"),
    YML("yml"),
    JSON("json"),
    XML("xml"),
    TXT("txt"),
    RAW(""),
    INVALID("");

    private final String value;

    EnumWConfigFileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EnumWConfigFileType fromValue(String value) {
        switch (value) {
            case "properties": return PROPERTIES;
            case "yml": return YML;
            case "xml": return XML;
            case "json": return JSON;
            case "txt": return TXT;
            case "": return RAW;
            default: return INVALID;
        }
    }
}
