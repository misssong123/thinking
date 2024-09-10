package netty.wconfig.client.exceptions;

public class WConfigClientTimeoutException extends Exception{

    public WConfigClientTimeoutException(String message) {
        super(message);
    }

    public WConfigClientTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
