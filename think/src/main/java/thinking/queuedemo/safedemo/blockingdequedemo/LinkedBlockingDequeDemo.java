package thinking.queuedemo.safedemo.blockingdequedemo;

import java.util.concurrent.*;

/**
 * 默认容量为 Integer.MAX_VALUE
 * 不允许 null 元素
 * ReentrantLock 实现
 */
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
        }
    }
}
class ConsumerAndProducerDemo {
    private static final LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>(5); // 容量为5

    public static void main(String[] args) {
        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    deque.putFirst("Task-" + i); // 头部插入
                    System.out.println("生产: Task-" + i);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String task = deque.takeLast(); // 尾部取出（FIFO）
                    System.out.println("消费: " + task);
                    TimeUnit.MILLISECONDS.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
