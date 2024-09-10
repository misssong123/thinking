package netty.wconfig.client.agentclient.session;

import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.protocol.ClientFrame;
import netty.wconfig.client.exceptions.WConfigClientException;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RequestSessionTracker {

    private static final ConcurrentHashMap<Integer, RequestSession> SYNC_REQUEST_MAPPING = new ConcurrentHashMap<>();

    public static void submit(int requestId) {
        SYNC_REQUEST_MAPPING.put(requestId, new RequestSession());
    }

    /**
     * @return null if timeout
     * */
    public static ClientFrame getRespondedFrame(int requestId, long timeoutInMillis) throws WConfigClientException {
        RequestSession session = SYNC_REQUEST_MAPPING.get(requestId);
        if (Objects.isNull(session)) {
            throw new WConfigClientException("request session not found, request id: " + requestId);
        }

        ClientFrame frame = session.getFrame(timeoutInMillis);
        removeSession(requestId);
        return frame;
    }

    public static RequestSession getSession(int requestId) {
        return SYNC_REQUEST_MAPPING.get(requestId);
    }

    public static void removeSession(int requestId) {
        SYNC_REQUEST_MAPPING.remove(requestId);
    }
}

