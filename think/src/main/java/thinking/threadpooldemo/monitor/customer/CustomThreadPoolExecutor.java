package thinking.threadpooldemo.monitor.customer;
import lombok.Getter;
import thinking.threadpooldemo.monitor.execute.Executor;
import thinking.threadpooldemo.monitor.config.CustomThreadPoolConfig;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
@Getter
public class CustomThreadPoolExecutor extends ThreadPoolExecutor implements Executor {
    private final String threadPoolName;
    private CustomThreadPoolConfig customThreadPoolConfig;

    public CustomThreadPoolExecutor(CustomThreadPoolConfig configuration) {
        super(
                configuration.getCoreSize(),
                configuration.getMaxSize(),
                configuration.getKeepAliveTime(),
                configuration.getTimeUnit(),
                configuration.getInitialQueueSize() == 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(configuration.getInitialQueueSize()),
                configuration.getThreadFactory(),
                configuration.getRejectedExecutionHandler()
        );
        this.threadPoolName = configuration.getThreadPoolName();
        this.customThreadPoolConfig = configuration;
    }

    /**
     * 更新线程池配置
     * @param configuration
     */
    public void renewCustomerThreadPoolExecutor(CustomThreadPoolConfig configuration) {
        if (configuration.getCoreSize() != this.getCorePoolSize()) {
            this.setCorePoolSize(configuration.getCoreSize());
        }
        if (configuration.getMaxSize() != this.getMaximumPoolSize()) {
            this.setMaximumPoolSize(configuration.getMaxSize());
        }
        if (configuration.getKeepAliveTime() != this.customThreadPoolConfig.getKeepAliveTime()
                || configuration.getTimeUnit() != this.customThreadPoolConfig.getTimeUnit()) {
            this.setKeepAliveTime(configuration.getKeepAliveTime(), configuration.getTimeUnit());
        }
        if (!configuration.getRejectedExecutionHandler().getClass().getSimpleName().equals(this.getRejectedExecutionHandler().getClass().getSimpleName())) {
            this.setRejectedExecutionHandler(configuration.getRejectedExecutionHandler());
        }
        this.customThreadPoolConfig = configuration;
    }
}
