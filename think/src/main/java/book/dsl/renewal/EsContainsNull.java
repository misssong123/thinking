package book.dsl.renewal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EsContainsNull {
    String triggerValue() default "0";    // 触发包含null条件的特殊值
    String fieldName() default "";       // ES字段名（默认取Java字段名）
}
