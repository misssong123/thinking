package netty.wconfig.client.agentclient.session;

import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.agentclient.handlers.ClientInboundHandler;
import netty.wconfig.client.agentclient.protocol.ClientFrame;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RequestSession {
    private final CountDownLatch latch = new CountDownLatch(1);
    private ClientFrame frame = null;

    public void setFrame(ClientFrame frame) {
        this.frame = frame;
        latch.countDown();
    }

    /**
     * @return response set by {@link ClientInboundHandler}, null if this.latch await timeout
     * */
    public ClientFrame getFrame(long timeoutInMillis) {
        try {
            latch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("waiting request interrupted");
        }

        return frame;
    }
}

