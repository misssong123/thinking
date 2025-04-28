package thinking.queuedemo.unsafedemo.dequedemo;

import java.util.LinkedList;

/**
 * 基于链表实现
 * 允许添加null元素
 * 在插入和删除元素时性能较好，时间复杂度为O(1)
 */
public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<Integer> queue = new LinkedList<>();
        for(int i = 0 ; i < 20 ; i++){
            queue.add((int)(Math.random()*1000));
            queue.addFirst(i);
            queue.addLast(i);
        }
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }
        System.out.println(queue.poll());
    }
}
