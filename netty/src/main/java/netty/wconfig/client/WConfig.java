package netty.wconfig.client;

import netty.wconfig.client.configs.ClientConfig;
import netty.wconfig.client.enums.EnumWConfigFileType;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static netty.wconfig.client.constants.ClientConstants.NAMESPACE_NAME_DEFAULT;
import static netty.wconfig.client.enums.EnumWConfigFileType.PROPERTIES;

@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WConfig {

    String namespace() default NAMESPACE_NAME_DEFAULT;

    /**
     *
     * */
    boolean subscribe() default false;

    /**
     * 默认使用文件 ./{@link ClientConfig#getLocalFileFolder()}/{@link WConfig#namespace()}.{@link WConfig#fileType()}
     * */
    String localFile() default "";

    EnumWConfigFileType fileType() default PROPERTIES;
}
