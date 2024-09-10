package netty.wconfig.client.agentclient.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import netty.wconfig.client.enums.EnumClientVersion;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientFrame {

    private byte type;
    private byte version = EnumClientVersion.V3.getValue();
    private int requestId;
    private Object message;

}
