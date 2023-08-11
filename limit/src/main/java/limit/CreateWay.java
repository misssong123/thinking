package limit;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现方式
 * 1.固定窗口算法
 * 2.滑动窗口算法
 * 3.滑动日志算法
 * 4.漏桶算法
 * 5.令牌桶算法
 * 6.Redis 分布式限流
 */
public class CreateWay {
    public static void main(String[] args) {

    }
}

/**
 * 固定窗口算法
 * 固定窗口算法又叫计数器算法，是一种简单方便的限流算法。主要通过一个支持原子操作的计数器来累计 1 秒内的请求次数，
 * 当 1 秒内计数达到限流阈值时触发拒绝策略。每过 1 秒，计数器重置为 0 开始重新计数。
 * 缺点:遇到时间窗口的临界突变时,会导致无法达到限制要求
 */
class FixedWindow{
    //时间间隔
    private static final int TIME_WINDOWS = 1000;
    //起始时间
    private static long startTime = System.currentTimeMillis();
    //限制次数
    private static final int QPS = 2;
    //数字记录
    private static AtomicInteger num = new AtomicInteger(0);
    public static synchronized boolean tryAcquire(){
        if (System.currentTimeMillis() - startTime >= TIME_WINDOWS){
            startTime = System.currentTimeMillis();
            num.set(0);
        }
        return num.incrementAndGet()<=QPS;
    }
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(400);//手动控制时间间隔
        for (int i = 0 ; i < 20 ; i++){
            LocalTime now = LocalTime.now();
            Thread.sleep(200);
            if (tryAcquire()){
                System.out.println("now:"+now+";正常执行。。。。");
            }else {
                System.out.println("now:"+now+";数据限流。。。。");
            }
        }
    }
}

/**
 * 滑动窗口算法
 */
class SlidingWindow{
    /**
     * 阈值
     */
    private  int qps = 2;
    /**
     * 时间窗口总大小（毫秒）
     */
    private long windowSize = 1000;
    /**
     * 多少个子窗口
     */
    private Integer windowCount = 10;
    /**
     * 窗口列表
     */
    private WindowInfo[] windowArray = new WindowInfo[windowCount];

    public SlidingWindow() {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < windowArray.length; i++) {
            windowArray[i] = new WindowInfo(currentTimeMillis, new AtomicInteger(0));
        }
    }

    /**
     * 1. 计算当前时间窗口
     * 2. 更新当前窗口计数 & 重置过期窗口计数
     * 3. 当前 QPS 是否超过限制
     *
     * @return
     */
    public  synchronized boolean tryAcquire() {
        long currentTimeMillis = System.currentTimeMillis();
        // 1. 计算当前时间窗口
        int currentIndex = (int)(currentTimeMillis % windowSize / (windowSize / windowCount));
        // 2.  更新当前窗口计数 & 重置过期窗口计数
        int sum = 0;
        for (int i = 0; i < windowArray.length; i++) {
            WindowInfo windowInfo = windowArray[i];
            if ((currentTimeMillis - windowInfo.getTime()) > windowSize) {
                windowInfo.getNumber().set(0);
                windowInfo.setTime(currentTimeMillis);
            }
            if (currentIndex == i && windowInfo.getNumber().get() < qps) {
                windowInfo.getNumber().incrementAndGet();
            }
            sum = sum + windowInfo.getNumber().get();
        }
        // 3. 当前 QPS 是否超过限制
        return sum <= qps;
    }
    @Data
    private class WindowInfo {
        // 窗口开始时间
        public Long time;
        // 计数器
        public AtomicInteger number;

        public WindowInfo(long time, AtomicInteger number) {
            this.time = time;
            this.number = number;
        }
    }
    public static void main(String[] args) throws Exception {
        SlidingWindow demo = new SlidingWindow();
        Thread.sleep(400);//手动控制时间间隔
        for (int i = 0 ; i < 20 ; i++){
            LocalTime now = LocalTime.now();
            Thread.sleep(200);
            if (demo.tryAcquire()){
                System.out.println("now:"+now+";正常执行。。。。");
            }else {
                System.out.println("now:"+now+";数据限流。。。。");
            }
        }
    }
}

/**
 * 滑动日志算法
 * 动日志算法是实现限流的另一种方法，这种方法比较简单。基本逻辑就是记录下所有的请求时间点，新请求到来时先判断
 * 最近指定时间范围内的请求数量是否超过指定阈值，
 * 由此来确定是否达到限流，这种方式没有了时间窗口突变的问题，限流比较准确，但是因为要记录下每次请求的时间点，所以占用的内存较多。
 */
class SlidingLog{
    /**
     * 阈值
     */
    private  int qps = 5;
    /**
     * 时间窗口总大小（毫秒）
     */
    private long windowSize = 1000;

    /**
     * 成功请求缓存
     * @param args
     */
    private LinkedList<Long> cache = new LinkedList<>();
    public synchronized boolean tryAcquire(){
        while (!cache.isEmpty()&&(System.currentTimeMillis() - cache.peek()>=windowSize)){
            cache.pop();
        }
        if (cache.size() >= qps){
            return false;
        }
        cache.push(System.currentTimeMillis());
        return true;
    }
    public static void main(String[] args) throws InterruptedException {
        SlidingLog demo = new SlidingLog();
        Thread.sleep(400);
        for (int i = 0 ; i < 20 ; i ++){
            Thread.sleep(150);
            LocalTime now = LocalTime.now();
            if (demo.tryAcquire()){
                System.out.println("now:"+now+";正常执行。。。。");
            }else {
                System.out.println("now:"+now+";数据限流。。。。");
            }
        }
    }
}

/**
 * 漏桶算法
 * 漏桶算法是一种常见的限流算法之一，它以一个固定容量的桶为基础来控制数据的流出速度。桶有一个固定的容量，
 * 类似于水桶，而数据请求则被看作是水，以恒定的速率不断流入桶中，当桶满了以后，多余的水就会溢出，即被丢弃掉。
 * 这个过程中，任何时候对桶的操作都是加水和漏水两个操作，也就是往桶里加请求，往外漏请求，漏水的速率是固定的，
 * 与请求的速率无关，所以保证了流出的速率是恒定的。
 *  利用漏桶算法可以有效地控制请求的访问速率，从而防止服务过载。当请求的速率超过了规定的速率，
 *  漏桶将漏出大量的请求，导致访问速率下降，从而达到控制请求的目的。同时，漏桶算法也能够应对突发流量的处理，
 *  因为无论请求的速率多快，保证的是每秒钟最多只能漏出固定数量的请求，从而保证了系统的稳定性。
 */
class LeakyBucket{
    private long lastLeakTime;
    private final long capacity;
    private long waterLevel;

    public LeakyBucket(long capacity) {
        this.capacity = capacity;
        this.waterLevel = 0;
        this.lastLeakTime = System.nanoTime();
    }

    public synchronized boolean tryAcquire(long amount) {
        leak();
        if (waterLevel + amount <= capacity) {
            waterLevel += amount;
            return true;
        } else {
            return false;
        }
    }

    private void leak() {
        long now = System.nanoTime();
        long delta = now - lastLeakTime;
        long nanosPerDrop = delta / capacity;
        waterLevel = Math.max(0, waterLevel - nanosPerDrop);
        lastLeakTime = now;
    }

    public static void main(String[] args) throws Exception {
        LeakyBucket bucket = new LeakyBucket(1000);
        for (int i = 0; i < 100; i++) {
            boolean acquired = bucket.tryAcquire(50);
            System.out.println("Attempt " + i + ": " + (acquired ? "Success" : "Failed"));
            TimeUnit.MILLISECONDS.sleep(50);
        }
    }
}

/**
 * 令牌桶算法
 * 令牌桶算法同样是实现限流是一种常见的思路，最为常用的 Google 的 Java 开发工具包 Guava 中的限流工具类 RateLimiter 就是令牌桶的一个实现。
 * 令牌桶的实现思路类似于生产者和消费之间的关系。
 * 系统服务作为生产者，按照指定频率向桶（容器）中添加令牌，如 QPS 为 2，每 500ms 向桶中添加一个令牌，如果桶中令牌数量达到阈值，则不再添加。
 * 请求执行作为消费者，每个请求都需要去桶中拿取一个令牌，取到令牌则继续执行；如果桶中无令牌可取，
 * 就触发拒绝策略，可以是超时等待，也可以是直接拒绝本次请求，由此达到限流目的。
 */
class TokenBucket {
    public static void main(String[] args) throws Exception{
        RateLimiter rateLimiter = RateLimiter.create(2);
        for (int i = 0; i < 10; i++) {
            LocalDateTime time = LocalDateTime.now();
            System.out.println(time + ":" + rateLimiter.tryAcquire());
            Thread.sleep(250);
        }
    }
}

/**
 * Redis 分布式限流
 */
class RedisWay{
    public static void main(String[] args) {

    }
}
