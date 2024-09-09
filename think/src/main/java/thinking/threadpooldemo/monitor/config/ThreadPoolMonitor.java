package thinking.threadpooldemo.monitor.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class ThreadPoolMonitor {
    private String poolName;
    private int corePoolSize;
    private int poolSize;
    private int maximumPoolSize;
    private int activeCount;
    private int largestPoolSize;
    private long completedTaskCount;
    private int queueSize;
    private long keepAliveTime;
    private boolean isShutdown;
    private boolean isTerminated;
    private String queueType;
    private String rejectedExecutionHandler;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS")
    private Date monitorTime;
    private String timeUnit;
}
