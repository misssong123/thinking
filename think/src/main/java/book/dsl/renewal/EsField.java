package book.dsl.renewal;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 字段映射注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EsField {
    String name() default "";         // ES字段名（默认与属性名相同）
    QueryType type() default QueryType.AUTO; // 查询类型
    boolean sortable() default false;
}
