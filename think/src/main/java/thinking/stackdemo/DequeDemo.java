package thinking.stackdemo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 支持前后插入和弹出元素
 */
public class DequeDemo {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();//默认16
        for(int i = 0 ; i < 10 ; i++){
            stack.push(i);
        }
        while (!stack.isEmpty()){
            System.out.println(stack.pop());
        }
        System.out.println(stack.pop());//NoSuchElementException
    }
}
