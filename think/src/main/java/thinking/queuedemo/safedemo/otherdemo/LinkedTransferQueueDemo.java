package thinking.queuedemo.safedemo.otherdemo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于链表的无界阻塞队列，与SynchronousQueue类似，也是一个线程安全的队列，
 * 容量为Integer.MAX_VALUE
 * 基于cas实现的无锁队列
 * 当一个线程调用 transfer 方法时，它会尝试直接把元素传递给正在等待接收元素的消费者线程
 */
public class LinkedTransferQueueDemo {
    /**
     *入队方法
     * add(E e)：将指定元素插入到队列尾部。如果队列容量无限（LinkedTransferQueue 是无界队列），此方法总是会成功并返回 true。若队列容量有限且已满，会抛出 IllegalStateException 异常。
     * offer(E e)：将指定元素插入到队列尾部，无论何时调用此方法都会立即返回 true，因为 LinkedTransferQueue 是无界队列。
     * put(E e)：将指定元素插入到队列尾部。由于是无界队列，该方法不会阻塞，会立即将元素插入队列。
     * offer(E e, long timeout, TimeUnit unit)：和 offer(E e) 类似，由于队列无界，总是会立即插入元素并返回 true，不会受超时时间影响。
     * transfer(E e)：尝试将元素直接传输给等待的消费者。如果有消费者正在等待接收元素，则直接将元素传递给该消费者；否则，当前线程会被阻塞，直到有消费者接收该元素。
     * tryTransfer(E e)：尝试将元素直接传输给等待的消费者。如果有消费者正在等待接收元素，则直接将元素传递给该消费者并返回 true；否则，将元素插入队列尾部并返回 false，不会阻塞当前线程。
     * tryTransfer(E e, long timeout, TimeUnit unit)：在指定的时间内尝试将元素直接传输给等待的消费者。如果在等待时间内有消费者接收该元素，则返回 true；否则，将元素插入队列尾部并返回 false。
     * 出队方法
     * remove()：移除并返回队列头部的元素。若队列为空，会抛出 NoSuchElementException 异常。
     * poll()：移除并返回队列头部的元素。若队列为空，返回 null。
     * take()：移除并返回队列头部的元素。若队列为空，当前线程会被阻塞，直到队列中有元素可供移除。
     * poll(long timeout, TimeUnit unit)：在指定的时间内尝试移除并返回队列头部的元素。若在等待时间内队列中有元素，则移除并返回该元素；否则，返回 null。
     * 查看元素方法
     * element()：返回队列头部的元素，但不移除。若队列为空，会抛出 NoSuchElementException 异常。
     * peek()：返回队列头部的元素，但不移除。若队列为空，返回 null。
     */
    public static void main(String[] args) throws InterruptedException{
        LinkedTransferQueue<Integer> queue = new LinkedTransferQueue<>();
        // 向队列中添加元素
        queue.offer(1);
        queue.offer(2);

        // 生产者线程，调用 transfer 方法传递元素
        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 3; i <= 5; i++) {
                    System.out.println("Producer is trying to transfer: " + i);
                    queue.transfer(i);
                    System.out.println("Producer transferred: " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 消费者线程，调用 take 方法获取元素
        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Integer element = queue.take();
                    System.out.println("Consumer took: " + element);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start();
        // 给生产者线程一些时间启动 transfer 操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class LinkedTransferQueueProducer implements Runnable {
    private final LinkedTransferQueue<Integer> queue;
    private final CountDownLatch latch;

    public LinkedTransferQueueProducer(LinkedTransferQueue<Integer> queue, CountDownLatch latch) {
        this.queue = queue;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println("transfer: " + (i+3));
                queue.transfer(i+3);
                // 第一次 transfer 完成后，释放 CountDownLatch
                if (i == 0) {
                    latch.countDown();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class LinkedTransferQueueConsumer implements Runnable {
    private final LinkedTransferQueue<Integer> queue;
    private final CountDownLatch latch;

    public LinkedTransferQueueConsumer(LinkedTransferQueue<Integer> queue, CountDownLatch latch) {
        this.queue = queue;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            if (!latch.await(1, TimeUnit.SECONDS)) {
                System.err.println("Consumer timed out waiting for producer");
            }
        } catch (InterruptedException e) {
            System.err.println("Consumer interrupted while waiting: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        for (int i = 0; i < 5; i++) {
            try {
                // 尝试创建一个虚拟元素来检查是否有等待的 transfer 操作
                System.out.println("task (from queue): " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ProducerConsumerExample {
    public static void main(String[] args) {
        // 创建一个 CountDownLatch，初始计数为 1
        CountDownLatch latch = new CountDownLatch(1);
        LinkedTransferQueue<Integer> queue = new LinkedTransferQueue<>();
        Thread producerThread = new Thread(new LinkedTransferQueueProducer(queue,latch));
        Thread consumerThread = new Thread(new LinkedTransferQueueConsumer(queue,latch));
        producerThread.start();
        consumerThread.start();
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All elements consumed.");
    }
}
