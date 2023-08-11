package main.java.thinking.queuedemo;

import java.util.LinkedList;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<Integer> queue = new LinkedList<>();
        for(int i = 0 ; i < 20 ; i++){
            queue.add((int)(Math.random()*1000));
        }
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }
        System.out.println(queue.poll());//null
    }
}
