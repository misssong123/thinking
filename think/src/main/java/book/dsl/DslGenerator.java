package book.dsl;

import book.dsl.param.SortVo;
import book.dsl.renewal.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DslGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static String buildQuery(Object param) throws Exception {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode bool = mapper.createObjectNode();
        ArrayNode must = mapper.createArrayNode();
        // 反射遍历所有字段
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(param);
            // 跳过空值和被忽略的字段
            if (Objects.isNull(value) || field.isAnnotationPresent(EsIgnoreField.class)){
                continue;
            }
            // 处理范围查询,仅支持list类型
            if (field.isAnnotationPresent(EsRange.class)) {
                if (value instanceof List) {
                    handleRangeQuery((List)value, field, must);
                }
                continue;
            }
            // 优先处理包含null条件的字段
            if (field.isAnnotationPresent(EsContainsNull.class)) {
                handleContainsNullAnnotation(field, value, must);
                continue;
            }
            // 处理普通字段
            EsField esField = field.getAnnotation(EsField.class);
            if (esField == null) {
                continue;
            }
            // 处理普通字段
            QueryType queryType = esField.type();
            String esFieldName = getEsFieldName(field, esField);
            // 自动推断查询类型
            if (queryType == QueryType.AUTO) {
                queryType = inferQueryType(field);
            }
            // 构建查询条件
            buildQueryNode(must, queryType, esFieldName, value);
        }

        // 添加必须条件
        if (!must.isNull()) {
            bool.set("must", must);
            root.set("query", mapper.createObjectNode().set("bool", bool));
        }
        //查询排序字段
        try {
            Field sortVoList = param.getClass().getDeclaredField("sortVoList");
            handleSort(sortVoList, param, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 处理分页
        handlePagination(param, root);
        return root.toString();
    }
    private static void handleContainsNullAnnotation(Field field, Object value, ArrayNode must) {
        EsContainsNull annotation = field.getAnnotation(EsContainsNull.class);
        List<?> listValue = (List<?>) value;
        if (CollectionUtils.isEmpty(listValue)) {
            return;
        }
        String esFieldName = getEsFieldName(field, annotation);
        String trigger = annotation.triggerValue();
        // 构建should条件
        ObjectNode boolNode = mapper.createObjectNode();
        ArrayNode should = mapper.createArrayNode();
        // 条件1：terms查询（排除triggerValue）
        List<Object> filteredValues = listValue.stream()
                .filter(v -> !v.toString().equals(trigger))
                .collect(Collectors.toList());
        if (!filteredValues.isEmpty()) {
            should.add(mapper.createObjectNode()
                    .set("terms", mapper.createObjectNode()
                            .set(esFieldName, mapper.valueToTree(filteredValues))));
        }
        // 条件2：字段为null或不存在（当包含triggerValue时）
        if (listValue.contains(trigger)) {
            should.add(mapper.createObjectNode()
                    .set("bool", mapper.createObjectNode()
                            .set("must_not", mapper.createObjectNode()
                                    .set("exists", mapper.createObjectNode()
                                            .put("field", esFieldName)))));
        }
        // 至少满足一个条件
        boolNode.set("should", should);
        boolNode.put("minimum_should_match", 1);
        must.add(boolNode);
    }
    // 处理排序逻辑
    private static void handleSort(Field sortVoListField,Object param, ObjectNode root) throws IllegalAccessException {
        ArrayNode sortArray = mapper.createArrayNode();
        // 1. 处理显式排序字段
        if (sortVoListField != null) {
            sortVoListField.setAccessible(true);
            List<SortVo> sortVoList = (List<SortVo>) sortVoListField.get(param);
            if (CollectionUtils.isNotEmpty(sortVoList)) {
                for (SortVo sortVo : sortVoList) {
                    ObjectNode sortNode = mapper.createObjectNode().put(sortVo.getFieldName(), Boolean.TRUE.equals(sortVo.getAsc()) ? "asc" : "desc");
                    sortArray.add(sortNode);
                }
            }
        }
        root.set("sort", sortArray);
    }
    // 处理分页逻辑
    private static void handlePagination(Object param, ObjectNode root) throws IllegalAccessException {
        int pageSize = 10;
        int pageIndex = 1;
        Object assistValue = null;
        Object secondAssistValue = null;

        // 反射获取分页参数
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            switch (field.getName()) {
                case "pageSize":
                    pageSize = field.get(param) != null ? (Integer) field.get(param) : 10;
                    break;
                case "pageIndex":
                    pageIndex = field.get(param) != null ? (Integer) field.get(param) : 1;
                    break;
                case "assistValue":
                    assistValue = field.get(param);
                    break;
                case "secondAssistValue":
                    secondAssistValue = field.get(param);
                    break;
            }
        }
        // 深度分页判断
        if (pageSize * pageIndex > 10000) {
            root.put("size", pageSize);
            if (assistValue != null || secondAssistValue != null) {
                ArrayNode searchAfter = mapper.createArrayNode();
                if (assistValue != null) searchAfter.add(assistValue.toString());
                if (secondAssistValue != null) searchAfter.add(secondAssistValue.toString());
                root.set("search_after", searchAfter);
            }
        } else {
            root.put("from", (pageIndex - 1) * pageSize);
            root.put("size", pageSize);
        }
    }
    private static void handleRangeQuery(List value, Field field, ArrayNode must) {
        if (CollectionUtils.isEmpty(value)|| value.size() < 2) {
            return;
        }
        EsRange esRange = field.getAnnotation(EsRange.class);
        String start =  value.get(0).toString();
        String end =  value.get(1).toString();
        if (StringUtils.isNoneBlank(start, end)) {
            must.add(mapper.createObjectNode()
                    .<ObjectNode>set("range", mapper.createObjectNode()
                            .<ObjectNode>set(esRange.field(), mapper.createObjectNode()
                                    .put("gte", start)
                                    .put("lte", end))));
        }
    }
    private static void buildQueryNode(ArrayNode must, QueryType type, String field, Object value) {
        switch (type) {
            case TERM:
                must.add(mapper.createObjectNode()
                        .set("term", mapper.createObjectNode().put(field, value.toString())));
                break;
            case TERMS:
                ArrayNode values = mapper.valueToTree(value);
                must.add(mapper.createObjectNode()
                        .set("terms", mapper.createObjectNode().set(field, values)));
                break;
        }
    }

    // 辅助方法：推断字段类型
    private static QueryType inferQueryType(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            return QueryType.TERMS;
        }
        return QueryType.TERM;
    }

    // 辅助方法：获取ES字段名
    private static String getEsFieldName(Field field, EsField esField) {
        return StringUtils.isBlank(esField.name()) ? field.getName() : esField.name();
    }
    private static String getEsFieldName(Field field, EsContainsNull annotation) {
        return StringUtils.isBlank(annotation.fieldName()) ?
                field.getName() : annotation.fieldName();
    }
}
