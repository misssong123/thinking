package thinking.threadpooldemo.monitor.execute;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import thinking.threadpooldemo.monitor.config.CustomThreadPoolConfig;
import thinking.threadpooldemo.monitor.config.LocalThreadPoolConfig;
import thinking.threadpooldemo.monitor.customer.CustomCaller;
import thinking.threadpooldemo.monitor.customer.CustomThreadPoolExecutor;
import thinking.threadpooldemo.monitor.enums.RejectExecutionPolicyEnum;
import thinking.threadpooldemo.monitor.manager.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutor {

    public static void registerExecutor(String threadConfigContent){
        // 1. 解析配置文件
        CustomThreadPoolConfig customThreadPoolConfig = parse(threadConfigContent);
        // 2. 注册线程池
        run(customThreadPoolConfig);
    }
    public static CustomThreadPoolConfig parse(String content) {
        LocalThreadPoolConfig local = JSONObject.parseObject(content, LocalThreadPoolConfig.class);
        CustomThreadPoolConfig config = new CustomThreadPoolConfig();
        try{
            BeanUtils.copyProperties(config, local);
        }catch (Exception ignored){
        }
        // 生成ThreadFactory
        config.setThreadFactory(new ThreadFactory() {
            final AtomicInteger currentThreadNum = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ThreadPool-" + config.getThreadPoolName() + "-" + currentThreadNum.getAndIncrement());
            }
        });
        // 生成拒绝策略
        RejectExecutionPolicyEnum policyEnum = RejectExecutionPolicyEnum.getPolicyEnum(local.getRejectedExecutionHandlerName());
        config.setRejectedExecutionHandler(policyEnum.getPolicy());
        return config;
    }
    public static <V> List<Future<V>> batchSubmitTask(String threadPoolName, List<CustomCaller<V>> taskList) {
        if(CollectionUtils.isEmpty(taskList)){
            throw new IllegalStateException("您不能提交空任务");
        }
        CustomThreadPoolExecutor threadPoolExecutor = ThreadPoolManager.getInstance().getThreadPoolExecutor(threadPoolName);
        if(threadPoolExecutor == null){
            throw new IllegalStateException("系统未初始化该线程池[" + threadPoolName + "]，请检查是否已配置");
        }
        taskList.forEach(task -> task.setThreadPoolConfig(threadPoolExecutor.getCustomThreadPoolConfig()));
        List<Future<V>> futures = new ArrayList<>();
        try {
            futures.addAll(threadPoolExecutor.invokeAll(taskList));
        } catch (Exception ignored) {
        }
        return futures;
    }
    public static void run(CustomThreadPoolConfig customThreadPoolConfig) {
        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        threadPoolManager.createOrRefreshThreadPool(customThreadPoolConfig.getThreadPoolName(), customThreadPoolConfig);
    }
}
