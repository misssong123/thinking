package thinking.threadpooldemo.monitor.customer;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thinking.threadpooldemo.monitor.config.CustomThreadPoolConfig;

import java.util.concurrent.Callable;

public abstract class CustomCaller <V> implements Callable<V> {

    private static final Logger logger = LoggerFactory.getLogger(CustomCaller.class);

    @Getter
    private final String taskName;
    @Setter
    private CustomThreadPoolConfig threadPoolConfig;

    public CustomCaller(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public V call() throws Exception {
        StopWatch runnerWatch = StopWatch.createStarted();
        try{
            return process();
        } finally {
            runnerWatch.stop();
            // 记录埋点信息
            /*if(threadPoolConfig.isMonitor()){
                if (threadPoolConfig.getAvgTaskUseTimeId() > 0) {
                    System.out.printf("场景:【%s】,id:【%d】,time:【%d】%n","AVG",threadPoolConfig.getAvgTaskUseTimeId(), (int) runnerWatch.getTime());
                }
                if (threadPoolConfig.getMaxTaskUseTimeId() > 0) {
                    System.out.printf("场景:【%s】,id:【%d】,time:【%d】%n","MAX",threadPoolConfig.getMaxTaskUseTimeId(), (int) runnerWatch.getTime());
                }
                if (threadPoolConfig.getMinTaskUseTimeId() > 0) {
                    System.out.printf("场景:【%s】,id:【%d】,time:【%d】%n","MIN",threadPoolConfig.getMinTaskUseTimeId(), (int) runnerWatch.getTime());
                }
            }*/

            /*if(threadPoolConfig.isPrintLog()){
                logger.info(String.format("线程池[%s]的任务[%s]被线程[%s]执行耗时：%sms",
                        threadPoolConfig.getThreadPoolName(),
                        this.getTaskName(),
                        Thread.currentThread().getName(),
                        runnerWatch.getTime()));
            }*/
        }
    }
    public abstract V process() throws Exception;
}
