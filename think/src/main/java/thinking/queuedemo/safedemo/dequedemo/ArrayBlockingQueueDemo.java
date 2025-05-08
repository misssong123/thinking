package thinking.queuedemo.safedemo.dequedemo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 指定大小的阻塞队列
 * ReentrantLock 实现
 * 底层使用数组
 * 元素不允许为null
 */
public class ArrayBlockingQueueDemo {
    public static void main(String[] args) throws Exception{
        ArrayBlockingQueue<String> demo = new ArrayBlockingQueue<>(3,true);
        demo.put("a");
        demo.put("b");
        demo.put("c");
        System.out.println(demo);
        System.out.println(demo.take());
        System.out.println(demo.take());
        System.out.println(demo.take());
    }
}
class Producer implements Runnable {
    private final ArrayBlockingQueue<Integer> queue;

    public Producer(ArrayBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("Producing: " + i);
                queue.put(i);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final ArrayBlockingQueue<Integer> queue;

    public Consumer(ArrayBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                Integer item = queue.take();
                System.out.println("Consuming: " + item);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class ArrayBlockingQueueProducerConsumerExample {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
