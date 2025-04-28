package thinking.queuedemo.safedemo.priorityqueuedemo;

import java.util.PriorityQueue;

/**
 * 查看排序实现
 */
public class PriorityQueueDemo {
    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for(int i = 0 ; i < 20 ; i++){
            queue.add((int)(Math.random()*1000));
        }
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }
        System.out.println(queue.poll());//null
    }
}
