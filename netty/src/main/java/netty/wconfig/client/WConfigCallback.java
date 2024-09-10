package netty.wconfig.client;

import netty.wconfig.client.exceptions.WConfigClientException;

import java.util.Map;

public interface WConfigCallback {
    void callback(String namespace, Map<String, String> newConfigs) throws WConfigClientException;
}
