package netty.wconfig.client.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

public class MDCUtil {
    public static <T> Supplier<T> mdcWrapper(Supplier<T> supplier) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            MDC.setContextMap(mdc);
            return supplier.get();
        };
    }
}
