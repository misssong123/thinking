package thinking.jsondemo;

import thinking.jsondemo.obj.FullName;
import thinking.jsondemo.obj.SourceObject;
import thinking.jsondemo.obj.TargetObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SourceObject friend = new SourceObject();
        //初始化
        friend.setName("张三1");
        FullName  friendFullName = new FullName("李1","四1","人1");
        friend.setFullName(friendFullName);
        friend.setAge(21);
        friend.setBirthday(new Date());
        friend.setHobbies(Arrays.asList("篮球1","游泳1"));
        Map<String, String> clothes1 = new HashMap<>();
        clothes1.put("内裤1","红色1");
        clothes1.put("上衣1","黄色1");
        friend.setClothes(clothes1);
        //---------------------------------------------
        SourceObject source = new SourceObject();
        //初始化
        source.setName("张三");
        FullName  fullName = new FullName("李","四","人");
        source.setFullName(fullName);
        source.setAge(20);
        source.setBirthday(new Date());
        source.setHobbies(Arrays.asList("篮球","游泳"));
        Map<String, String> clothes = new HashMap<>();
        clothes.put("内裤","红色");
        clothes.put("上衣","黄色");
        source.setClothes(clothes);
        source.setFriends(Arrays.asList(friend));
        //fastjson
        String s = FastJsonDemo.bean2Json(source);
        TargetObject target = FastJsonDemo.json2Bean(s, TargetObject.class);
        System.out.println(FastJsonDemo.bean2Json(target));
        //System.out.println(target.getFriends().get(0).getClass());
        //Gson
        String s1 = GsonDemo.bean2Json(source);
        TargetObject targetObject = GsonDemo.json2Bean(s1, TargetObject.class);
        System.out.println(GsonDemo.bean2Json(targetObject));
        //System.out.println(targetObject.getFriends().get(0).getClass());
        //Jackson
        String s2 = JacksonDemo.bean2Json(source);
        TargetObject targetObject1 = JacksonDemo.json2Bean(s2, TargetObject.class);
        System.out.println(JacksonDemo.bean2Json(targetObject1));
        //System.out.println(targetObject1.getFriends().get(0).getClass());
        //JsonLib
        String s3 = JsonLibDemo.bean2Json(source);
        TargetObject targetObject2 = JsonLibDemo.json2Bean(s3, TargetObject.class);
        System.out.println(JsonLibDemo.bean2Json(targetObject2));
        //System.out.println(targetObject2.getFriends().get(0).getClass());
    }
}
