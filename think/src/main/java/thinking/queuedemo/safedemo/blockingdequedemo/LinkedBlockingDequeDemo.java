package thinking.queuedemo.safedemo.blockingdequedemo;

import java.util.concurrent.*;

public class LinkedBlockingDequeDemo {
    public static void main(String[] args) {
        LinkedBlockingDeque<String> blockingDeque = new LinkedBlockingDeque<>(3);
        try {
            blockingDeque.putFirst("Alice");
            blockingDeque.putLast("Bob");
            blockingDeque.putFirst("Charlie");

            System.out.println("BlockingDeque: " + blockingDeque);
            System.out.println("Take First: " + blockingDeque.takeFirst()); // Charlie
            System.out.println("BlockingDeque after takeFirst: " + blockingDeque);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //线程安全队列
            ConcurrentLinkedQueue<Integer> demo = new ConcurrentLinkedQueue<>();
            ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(10);
            SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
            PriorityBlockingQueue<Integer> priorityBlockingQueue = new PriorityBlockingQueue<>();
            DelayQueue delayQueue = new DelayQueue<>();
            LinkedTransferQueue linkedTransferQueue = new LinkedTransferQueue();
        }
    }
}
