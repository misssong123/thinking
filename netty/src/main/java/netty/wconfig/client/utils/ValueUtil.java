package netty.wconfig.client.utils;

import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.WConfigClient;
import netty.wconfig.client.constants.ClientConstants;
import org.springframework.core.env.Environment;

import java.util.Properties;
@Slf4j
public class ValueUtil {
    /**
     * 优先级：value本身 > 环境变量 > yml/properties
     *
     * @param value 属性
     * @return
     */
    public static String resolveEmbeddedValue(String value, Environment environment, boolean isSpring) {
        String valueTemp = value;
        if (value.contains("$")) {
            if (isSpring) {
                String[] $s = valueTemp.split("\\$");
                String envName = $s[1].substring(1, $s[1].length() - 1);
                valueTemp = System.getenv(envName);
                value = valueTemp == null ? environment.resolvePlaceholders(value) : valueTemp;
            } else {
                //从配置文件中读取
                try {
                    String envName = valueTemp.substring(2, valueTemp.length() - 1);
                    valueTemp = System.getenv(envName);
                    if (valueTemp == null) {
                        Properties properties = WConfigClient.readProperties(ClientConstants.SETTING_FILE_DEFAULT);
                        value = properties.getProperty(envName, "");
                    }
                } catch (Exception e) {
                    log.error("readProperties error", e);
                }
            }
        }
        return value;
    }
}
