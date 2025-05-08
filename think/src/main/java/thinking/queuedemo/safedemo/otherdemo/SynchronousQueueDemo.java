package thinking.queuedemo.safedemo.otherdemo;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue 内部使用了两种不同的队列实现，分别是基于先进先出（FIFO）的队列和基于后进先出（LIFO）的栈。
 * 公平模式下使用 FIFO 队列，非公平模式下使用 LIFO 栈。其核心机制是通过 Transferer
 * 接口的实现类来完成线程之间的元素传递和阻塞操作。
 * 不允许插入null元素
 * 容量为0
 * 插入元素的线程会被阻塞，直到有另一个线程从队列中取走一个元素
 * 取走元素的线程会被阻塞，直到有另一个线程向队列中插入一个元素
 * 适用于传递性，一个线程的输出作为另一个线程的输入
 */
public class SynchronousQueueDemo {
    /**
     *
     * 入队方法
     * put(E e)：将指定元素插入到队列中。如果没有其他线程正在等待接收该元素，当前线程会被阻塞，直到有另一个线程调用 take() 方法取走该元素。
     * offer(E e)：尝试将指定元素插入到队列中。如果有其他线程正在等待接收元素，则插入成功并返回 true；否则，立即返回 false，不会阻塞当前线程。
     * offer(E e, long timeout, TimeUnit unit)：尝试在指定的时间内将元素插入到队列中。如果在等待时间内有其他线程接收该元素，则插入成功并返回 true；否则，返回 false。
     *
     * 出队方法
     * take()：从队列中移除并返回一个元素。如果没有其他线程正在插入元素，当前线程会被阻塞，直到有另一个线程调用 put() 或 offer() 方法插入元素。
     * poll()：尝试从队列中移除并返回一个元素。如果有其他线程正在插入元素，则移除并返回该元素，返回 true；否则，立即返回 null，不会阻塞当前线程。
     * poll(long timeout, TimeUnit unit)：尝试在指定的时间内从队列中移除并返回一个元素。如果在等待时间内有其他线程插入元素，则移除并返回该元素，返回 true；否则，返回 false。
     */
    public static void main(String[] args) throws Exception{
        SynchronousQueue<Integer> queue = new SynchronousQueue<>(true);
        System.out.println(queue.offer(1));
        queue.put(1);
    }
}
class SynchronousQueueConsumer implements Runnable {
    private final SynchronousQueue<Integer> queue;
    public SynchronousQueueConsumer(SynchronousQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        for(int i = 0 ; i < 10  ; i++){
            try {
                TimeUnit.MILLISECONDS.sleep(150);
                System.out.println("消费者消费了" + queue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
class SynchronousQueueProducer implements Runnable {
    private final SynchronousQueue<Integer> queue;
    public SynchronousQueueProducer(SynchronousQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        for(int i = 0 ; i < 10  ; i++){
            try {
                System.out.println("生产者生产了" + i);
                queue.put(i);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
class SynchronousQueueTest{
    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<>(true);
        new Thread(new SynchronousQueueConsumer(queue)).start();
        new Thread(new SynchronousQueueProducer(queue)).start();
    }
}
