package netty.wconfig.client.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import netty.wconfig.client.exceptions.WConfigClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;

public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> T parse(String jsonString, Class<T> clazz) throws WConfigClientException {
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            String msg = MessageFormat.format("De-serialization exception with json {0} to class {1}", jsonString, clazz);
            log.error(msg, e);
            throw new WConfigClientException(msg, e);
        }
    }

    public static <T> T parse(String jsonString, TypeReference<T> typeReference) throws WConfigClientException {
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (IOException e) {
            String msg = MessageFormat.format("De-serialization exception with json {0} to type {1}", jsonString, typeReference);
            log.error(msg, e);
            throw new WConfigClientException(msg, e);
        }
    }

    public static <T> T parse(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, typeReference);
    }

    public static String toString(Object object) throws WConfigClientException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String msg = MessageFormat.format("Serialization exception with object of {0}", object.toString());
            log.error(msg, e);
            throw new WConfigClientException(msg, e);
        }
    }

    public static String toString(Map<String, String> object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
