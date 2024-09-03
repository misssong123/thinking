package thinking.threadlocaldemo;

import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskDecoratorDemo {
    public static void main(String[] args) {
        UserUtils.setUserId("1234");
        ThreadPoolTaskExecutor taskExecutor = test();
        System.out.println("父线程的用户信息"+UserUtils.getUserId());
        //执行异步任务，需要指定的线程池
        CompletableFuture.runAsync(()->
                System.out.println("子线程的用户信息"+UserUtils.getUserId()), taskExecutor);
    }
    public static ThreadPoolTaskExecutor test(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(2);
        //配置最大线程数
        executor.setMaxPoolSize(2);
        //配置队列大小
        executor.setQueueCapacity(100);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("thread-name-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //增加线程池修饰类
        executor.setTaskDecorator(new CustomTaskDecorator());
        //执行初始化
        executor.initialize();
        return executor;
    }
}

class CustomTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String robotId = UserUtils.getUserId();
        System.out.println(robotId);
        return () -> {
            try {
                // 将主线程的请求信息，设置到子线程中
                UserUtils.setUserId(robotId);
                // 执行子线程，这一步不要忘了
                runnable.run();
            } finally {
                // 线程结束，清空这些信息，否则可能造成内存泄漏
                UserUtils.clear();
            }
        };
    }
}