package netty.wconfig.client.exceptions;

public class WConfigClientException extends Exception{

    public WConfigClientException(String message) {
        super(message);
    }

    public WConfigClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
