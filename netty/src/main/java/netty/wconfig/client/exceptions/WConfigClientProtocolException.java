package netty.wconfig.client.exceptions;

public class WConfigClientProtocolException extends Exception{

    public WConfigClientProtocolException(String message) {
        super(message);
    }

    public WConfigClientProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
