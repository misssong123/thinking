package netty.wconfig.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来定义配置类的成员变量名称，来对应 portal 上配置的配置名称，可在 key 中包含 java 不支持的符号时使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WConfigField {
    String value();
}
