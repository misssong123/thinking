package thinking.queuedemo.unsafedemo.dequedemo;

import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Field;
import java.util.ArrayDeque;

/**
 * 基于数组进行实现
 * 不允许添加null元素
 * 在两端插入和删除元素的性能较好，时间复杂度为O(1)
 * 没有容量限制，会根据需要自动扩容。
 */
public class ArrayDequeDemo {
    public static void main(String[] args) throws Exception{
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for(int i = 7 ; i >=0 ; i--){
            deque.addFirst(i);
        }
        for (int i = 8 ; i < 15 ; i++){
            deque.addLast(i);
        }
        show(deque);
        deque.addLast(15);
    }
    public static void show(ArrayDeque<Integer> deque) throws Exception{
        Field elements = ArrayDeque.class.getDeclaredField("elements");
        elements.setAccessible(true);
        System.out.println(JSONObject.toJSONString(elements.get(deque)));
        Field head = ArrayDeque.class.getDeclaredField("head");
        head.setAccessible(true);
        System.out.println(JSONObject.toJSONString(head.get(deque)));
        Field tail = ArrayDeque.class.getDeclaredField("tail");
        tail.setAccessible(true);
        System.out.println(JSONObject.toJSONString(tail.get(deque)));
    }
}
