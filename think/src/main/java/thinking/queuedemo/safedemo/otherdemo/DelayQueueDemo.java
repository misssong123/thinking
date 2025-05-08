package thinking.queuedemo.safedemo.otherdemo;

import lombok.Getter;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 无界阻塞队列
 * DelayQueue 内部使用 PriorityQueue 作为存储元素的容器，并且使用一个 ReentrantLock 来保证线程安全。
 * PriorityQueue 会根据元素的延迟时间对元素进行排序，延迟时间最短的元素位于队列头部。
 * 当调用 take 等方法尝试从队列中取出元素时，会先检查队列头部元素的延迟时间是否到期，
 * 如果到期则取出该元素，否则线程会被阻塞，直到元素的延迟时间到期或者线程被中断。
 */
public class DelayQueueDemo {
    /**
     * put(E e)：将元素插入到队列中，该方法会调用 offer 方法，因为 DelayQueue 是无界队列，所以插入操作不会阻塞。
     * offer(E e)：将元素插入到队列中，由于队列无界，该方法总是返回 true。
     * take()：从队列中取出延迟时间到期的元素，如果队列为空或者队列头部元素的延迟时间未到期，线程会被阻塞，直到有元素的延迟时间到期。
     * poll()：从队列中取出延迟时间到期的元素，如果队列为空或者队列头部元素的延迟时间未到期，则返回 null。
     * poll(long timeout, TimeUnit unit)：在指定的时间内等待队列头部元素的延迟时间到期，如果到期则取出该元素，否则返回 null。
     */
    public static void main(String[] args) {
        DelayQueue<DelayDemo> queue = new DelayQueue<>();
        // 向队列中添加元素
        queue.offer(new DelayDemo(1000));
        queue.offer(new DelayDemo(2000));
        queue.offer(new DelayDemo(3000));
        // 从队列中取出元素
        new Thread(() -> {
            while(!queue.isEmpty()){
                DelayDemo poll = null;
                try {
                    poll = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("当前时间:"+System.currentTimeMillis()+";预期执行时间:"+poll.getExpireTime());
            }
        }).start();
    }
}
@Getter
class DelayDemo implements Delayed{
    private final long expireTime;
    public DelayDemo(long delayTime) {
        this.expireTime = System.currentTimeMillis() + delayTime;
    }
    @Override
    public long getDelay(TimeUnit unit) {
        long diffTime = expireTime - System.currentTimeMillis();
        return unit.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.expireTime,((DelayDemo)o).expireTime);
    }
}
