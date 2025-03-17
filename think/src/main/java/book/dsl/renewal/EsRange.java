package book.dsl.renewal;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 范围查询注解（用于时间字段）
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EsRange {
    String field();          // ES字段名
}

