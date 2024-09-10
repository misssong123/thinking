package netty.wconfig.client.agentclient;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import netty.wconfig.client.WConfigCallback;
import netty.wconfig.client.enums.EnumWConfigFileType;

import java.util.Set;

@ToString
@Data
@Builder
public class SubscriptionContext {
    private final Set<WConfigCallback> callBackSet;
    private final String localFile;
    private final EnumWConfigFileType fileType;
}
