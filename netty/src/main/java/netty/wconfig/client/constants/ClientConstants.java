package netty.wconfig.client.constants;

import netty.wconfig.client.WConfigCallback;

public interface ClientConstants {
    byte[] P_START_TAG = new byte[]{18, 17, 13, 10, 9};

    byte[] P_END_TAG = new byte[]{9, 10, 13, 17, 18};

    int HEAD_LEN = 10;

    int INT_LEN = 4;

    int HEAD_WO_MSG_LEN = HEAD_LEN - INT_LEN;

    byte PROTOCOL_VERSION = 3;

    String SETTING_FILE_DEFAULT = "config/wconfig-client.properties";

    String GROUP_NAME_DEFAULT = "default_group";

    String LOCAL_FOLDER_DEFAULT = "/tmp/wconfig-files/";

    String DEV_AGENT_HOST = "dev-agent-wconfig.58v5.cn";

    String NAMESPACE_NAME_DEFAULT = "default_namespace";

    String ITEM_KEY_DEFAULT = "default_key";

    WConfigCallback CALL_BACK_DEFAULT = (n, u) -> {};

    String CONFIG_LOCATOR_DELIMITER = "#";

    String GROUP_NAME_PATTERN = "\\w+";

    String NAMESPACE_NAME_PATTERN = "[\\da-zA-Z_/.-]+";
}
