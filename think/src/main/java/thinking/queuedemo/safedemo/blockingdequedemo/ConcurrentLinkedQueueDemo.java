package thinking.queuedemo.safedemo.blockingdequedemo;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * CAS实现
 * 容量：无界
 * 不允许null
 * 遵循 FIFO 原则
 */
public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> blockingDeque = new ConcurrentLinkedQueue<>();
        try {
            blockingDeque.add("Alice");
            blockingDeque.add("Bob");
            blockingDeque.add("Charlie");

            System.out.println("BlockingDeque: " + blockingDeque);
            System.out.println("Take First: " + blockingDeque.poll()); // Charlie
            System.out.println("BlockingDeque after takeFirst: " + blockingDeque);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Producer implements Runnable {
    private final ConcurrentLinkedQueue<Integer> queue;

    public Producer(ConcurrentLinkedQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            queue.offer(i);
            System.out.println("Produced: " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {
    private final ConcurrentLinkedQueue<Integer> queue;

    public Consumer(ConcurrentLinkedQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        Integer element;
        while ((element = queue.poll()) != null) {
            System.out.println("Consumed: " + element);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ProducerConsumerExample {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        Thread producerThread = new Thread(new Producer(queue));
        Thread consumerThread = new Thread(new Consumer(queue));

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}