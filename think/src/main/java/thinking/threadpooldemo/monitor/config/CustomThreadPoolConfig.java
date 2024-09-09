package thinking.threadpooldemo.monitor.config;

import lombok.Data;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class CustomThreadPoolConfig {
    protected static final String NEW_LINE = System.lineSeparator();
    private String threadPoolName;
    private String description;

    private int coreSize;
    private int maxSize;
    private int initialQueueSize;
    private long keepAliveTime;
    private TimeUnit timeUnit;

    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectedExecutionHandler;

    /* *********监控信息******* */
    /**
     *是否监控
     */
    private boolean monitor;
    /**
     * 是否打印日志
     */
    private boolean printLog;
    /**
     * 队列报警
     */
    private boolean needQueueAlarm;
    /**
     * 队列报警阈值
     */
    private Double queueAlarmThreshold;

    private int maxQueueSizeId;
    private int avgQueueSizeId;
    private int minQueueSizeId;

    private int maxActiveCountId;
    private int avgActiveCountId;
    private int minActiveCountId;

    private int maxLargestPoolSizeId;
    private int avgLargestPoolSizeId;
    private int minLargestPoolSizeId;

    private int maxPoolSizeId;
    private int avgPoolSizeId;
    private int minPoolSizeId;

    private int maxTaskUseTimeId;
    private int avgTaskUseTimeId;
    private int minTaskUseTimeId;
    public ThreadFactory getThreadFactory(String name) {
        return new ThreadFactory() {
            final AtomicInteger threadId = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name + "-" + threadId.incrementAndGet());
            }
        };
    }
}
