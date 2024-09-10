package netty.wconfig.client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import netty.wconfig.client.enums.EnumWConfigFileType;
import netty.wconfig.client.exceptions.WConfigClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static netty.wconfig.client.constants.ClientConstants.ITEM_KEY_DEFAULT;

public class LocalFileUtil {
    private static final Logger log = LoggerFactory.getLogger(LocalFileUtil.class);

    public static Map<String, String> readFile(EnumWConfigFileType fileType, String localFile) {

        try (InputStream inputStream = new FileInputStream(localFile)) {

            switch (fileType) {
                case PROPERTIES:
                    return readProperties(inputStream);
                case YML:
                    // todo: yml 类型的配置目前在系统中就是一个字符串，如果之后真正还原成 map 格式，再用以下方法
//                    return readYml(inputStream);
                case JSON:
                    // todo: json 类型的配置目前在系统中就是一个字符串，如果之后真正还原成 map 格式，再用以下方法
//                    return readJson(inputStream);
                default:
                    return readRaw(inputStream);
            }
        } catch (Throwable t) {
            log.error("", t);
        }

        return null;
    }

    public static void writeFile(EnumWConfigFileType fileType, String localFile, Map<String, String> newConfig) throws WConfigClientException {
        Path path = Paths.get(localFile);
        try {
            if (Objects.nonNull(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            log.error("write file error. localFile:{}", localFile, e);
            throw new WConfigClientException("", e);
        }

        switch (fileType) {
            case PROPERTIES:
                writeProperties(localFile, newConfig);
                break;
            case YML:
                // todo: yml 类型的配置目前在系统中就是一个字符串，如果之后真正还原成 map 格式，再用以下方法
//                writeYml(localFile, newConfig);
//                break;
            case JSON:
                // todo: json 类型的配置目前在系统中就是一个字符串，如果之后真正还原成 map 格式，再用以下方法
//                writeJson(localFile, newConfig);
//                break;
            default:
                writeRaw(localFile, newConfig);
        }
    }

    public static Map<String, String> readYml(InputStream inputStream) {
        Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }

    public static void writeYml(String localFile, Map<String, String> wconfig) {
        Yaml yaml = new Yaml();
        StringWriter stringWriter = new StringWriter();
        yaml.dump(wconfig, stringWriter);
        try (FileWriter fileWriter = new FileWriter(localFile)) {
            fileWriter.write(stringWriter.toString());
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    public static Map<String, String> readProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return Maps.newHashMap(Maps.fromProperties(properties));
    }

    public static void writeProperties(String localFile, Map<String, String> wconfig) {
        Properties properties = new Properties();
        properties.putAll(wconfig);
        try (FileWriter fileWriter = new FileWriter(localFile)) {
            properties.store(fileWriter, "");
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    public static Map<String, String> readJson(InputStream inputStream) throws IOException {
        return JsonUtil.parse(inputStream, new TypeReference<Map<String, String>>() {});
    }

    public static void writeJson(String localFile, Map<String, String> wconfig) {
        try (FileWriter fileWriter = new FileWriter(localFile)) {
            fileWriter.write(JsonUtil.toString(wconfig));
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static Map<String, String> readRaw(InputStream inputStream) throws IOException {
        Map<String, String> rawMap = new HashMap<>();
        rawMap.put(ITEM_KEY_DEFAULT, CharStreams.toString(new InputStreamReader(Objects.requireNonNull(inputStream), Charsets.UTF_8)));
        return rawMap;
    }

    public static void writeRaw(String localFile, Map<String, String> wconfig) {
        try (FileWriter fileWriter = new FileWriter(localFile)) {
            fileWriter.write(wconfig.get(ITEM_KEY_DEFAULT));
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}
