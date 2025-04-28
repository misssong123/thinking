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
        deque.addFirst(1);
        deque.addLast(2);
        deque.addFirst(3);
        show(deque);
        System.out.println(deque.removeFirst());
        System.out.println(deque.removeFirst());
        System.out.println(deque.removeFirst());
        System.out.println(deque);
    }
    public static void show(ArrayDeque<Integer> deque) throws Exception{
        Field elements = ArrayDeque.class.getDeclaredField("elements");
        elements.setAccessible(true);
        System.out.println(JSONObject.toJSONString(elements.get(deque)));
    }
}
