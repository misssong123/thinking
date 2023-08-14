package thinking.stackdemo;

import java.util.Stack;

/**
 * stack
 * 不建议使用
 */
public class StackDemo {
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        for(int i = 0 ; i < 10 ; i++){
            stack.push(i);
        }
        while (!stack.isEmpty()){
            System.out.println(stack.pop());
        }
        System.out.println(stack.pop());//EmptyStackException
    }
}
