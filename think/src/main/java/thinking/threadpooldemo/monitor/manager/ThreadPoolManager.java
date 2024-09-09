package thinking.threadpooldemo.monitor.manager;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thinking.threadpooldemo.monitor.execute.Executor;
import thinking.threadpooldemo.monitor.config.ThreadPoolMonitor;
import thinking.threadpooldemo.monitor.config.CustomThreadPoolConfig;
import thinking.threadpooldemo.monitor.customer.CustomThreadPoolExecutor;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static final ThreadPoolManager _instance = new ThreadPoolManager();

    public static ThreadPoolManager getInstance(){
        return _instance;
    }
    /**
     * 全局线程池
     */
    private static final Map<String, CustomThreadPoolExecutor> customerExecutors = new ConcurrentHashMap<>();

    public void createOrRefreshThreadPool(String threadName, CustomThreadPoolConfig config){

        CustomThreadPoolExecutor currentExecutor = customerExecutors.computeIfAbsent(threadName, key -> {
            CustomThreadPoolExecutor executor = new CustomThreadPoolExecutor(config);
            if (config.isMonitor()) {
                monitorThreadPool(executor);
            }
            return executor;
        });

        currentExecutor.renewCustomerThreadPoolExecutor(config);
    }
    /**
     * 监控线程池
     *
     */
    private void monitorThreadPool(final Executor executor) {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            Executor executorNew = customerExecutors.get(executor.getThreadPoolName()) != null
                    ? customerExecutors.get(executor.getThreadPoolName()) : executor;
            ThreadPoolMonitor threadPoolMonitor = buildThreadPoolMonitor(executorNew);
            if (executorNew instanceof CustomThreadPoolExecutor) {
                monitorCommonThreadPool((CustomThreadPoolExecutor) executorNew, threadPoolMonitor);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    private ThreadPoolMonitor buildThreadPoolMonitor(Executor executor) {
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor();
        threadPoolMonitor.setPoolName(executor.getThreadPoolName());

        if (executor instanceof CustomThreadPoolExecutor) {
            CustomThreadPoolExecutor customThreadPoolExecutor = (CustomThreadPoolExecutor) executor;
            TimeUnit timeUnit = customThreadPoolExecutor.getCustomThreadPoolConfig().getTimeUnit();
            threadPoolMonitor.setKeepAliveTime(customThreadPoolExecutor.getKeepAliveTime(timeUnit));
            threadPoolMonitor.setTimeUnit(timeUnit.name());
        }
        ThreadPoolExecutor targetExecutor = (ThreadPoolExecutor) executor;
        threadPoolMonitor.setActiveCount(targetExecutor.getActiveCount());
        threadPoolMonitor.setCorePoolSize(targetExecutor.getCorePoolSize());
        threadPoolMonitor.setLargestPoolSize(targetExecutor.getLargestPoolSize());
        threadPoolMonitor.setMaximumPoolSize(targetExecutor.getMaximumPoolSize());
        threadPoolMonitor.setCompletedTaskCount(targetExecutor.getCompletedTaskCount());
        threadPoolMonitor.setPoolSize(targetExecutor.getPoolSize());
        threadPoolMonitor.setTerminated(targetExecutor.isTerminated());
        threadPoolMonitor.setShutdown(targetExecutor.isShutdown());
        threadPoolMonitor.setQueueSize(targetExecutor.getQueue().size());
        threadPoolMonitor.setQueueType(targetExecutor.getQueue().getClass().getSimpleName());
        threadPoolMonitor.setRejectedExecutionHandler(targetExecutor.getRejectedExecutionHandler().getClass().getSimpleName());
        threadPoolMonitor.setMonitorTime(new Date());

        return threadPoolMonitor;
    }
    public CustomThreadPoolExecutor getThreadPoolExecutor(String threadPoolName){
        return customerExecutors.get(threadPoolName);
    }
    private void monitorCommonThreadPool(CustomThreadPoolExecutor customThreadPoolExecutor, ThreadPoolMonitor threadPoolMonitor) {
        CustomThreadPoolConfig configuration = customThreadPoolExecutor.getCustomThreadPoolConfig();

        if (configuration != null && configuration.isNeedQueueAlarm()) {
            double threshold = customThreadPoolExecutor.getCustomThreadPoolConfig().getInitialQueueSize() * configuration.getQueueAlarmThreshold();
            if (threadPoolMonitor.getQueueSize() >= threshold) {
                String fatalContent = String.format("线程池[%s]线程池队列长度参数[%d],当前队列长度[%d],超过预警值[%s]", threadPoolMonitor.getPoolName(), configuration.getInitialQueueSize(), threadPoolMonitor.getQueueSize(), threshold);
                logger.error(fatalContent);
            }
        }

        if (Objects.requireNonNull(configuration).isPrintLog()) {
            logger.info("线程池监控:{}", JSONObject.toJSONString(threadPoolMonitor));
        }

        /* ***上报*** */
        if (configuration.getMaxLargestPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MAXLargestPoolSize",configuration.getMaxLargestPoolSizeId(), threadPoolMonitor.getLargestPoolSize());
        }
        if (configuration.getAvgLargestPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","AVGLargestPoolSize",configuration.getAvgLargestPoolSizeId(), threadPoolMonitor.getLargestPoolSize());
        }
        if (configuration.getMinLargestPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MINLargestPoolSize",configuration.getMinLargestPoolSizeId(), threadPoolMonitor.getLargestPoolSize());
        }
        if (configuration.getMaxActiveCountId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MAXActiveCount",configuration.getMaxActiveCountId(), threadPoolMonitor.getActiveCount());
        }
        if (configuration.getAvgActiveCountId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","AVGActiveCount",configuration.getAvgActiveCountId(), threadPoolMonitor.getActiveCount());
        }
        if (configuration.getMinActiveCountId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MINActiveCount",configuration.getMinActiveCountId(), threadPoolMonitor.getActiveCount());
        }
        if (configuration.getMaxQueueSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MAXQueueSize",configuration.getMaxQueueSizeId(), threadPoolMonitor.getQueueSize());
        }
        if (configuration.getAvgQueueSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","AVGQueueSize",configuration.getAvgQueueSizeId(), threadPoolMonitor.getQueueSize());
        }
        if (configuration.getMinQueueSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MINQueueSize",configuration.getMinQueueSizeId(), threadPoolMonitor.getQueueSize());
        }
        if (configuration.getMaxPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MAXPoolSize",configuration.getMaxPoolSizeId(), threadPoolMonitor.getPoolSize());
        }
        if (configuration.getAvgPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","AVGPoolSize",configuration.getAvgPoolSizeId(), threadPoolMonitor.getPoolSize());
        }
        if (configuration.getMinPoolSizeId() > 0) {
            System.out.printf("场景:【%s】,id:【%d】,size:【%d】%n","MINPoolSize",configuration.getMinPoolSizeId(), threadPoolMonitor.getPoolSize());
        }
    }
}
