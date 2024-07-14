package thinking.jsondemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FastJsonDemo {
    public static String bean2Json(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
        return JSONObject.parseObject(jsonStr, objClass);
    }
    public static String beanJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T jsonBean(String jsonStr, Class<T> objClass) {
        return JSON.parseObject(jsonStr, objClass);
    }
}
