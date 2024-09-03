package thinking.threadlocaldemo;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransmittableThreadLocalDemo {
    static TransmittableThreadLocal<Integer> NUM = new TransmittableThreadLocal<>();
    public static void main(String[] args) throws Exception{
        NUM.set(12345);
        test2();
    }
    public static void test2() throws Exception{
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2,2,10L,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(3000));
        ExecutorService poolExecutor = TtlExecutors.getTtlExecutorService(pool);
        for(int i = 0 ; i < 4; i++){
            TimeUnit.SECONDS.sleep(1);
            poolExecutor.execute(() -> {
                System.out.println("name:"+Thread.currentThread().getName()+";【修改前】NUM:"+ NUM.get());
                NUM.set(NUM.get()+1);
                System.out.println("name:"+Thread.currentThread().getName()+";【修改后】NUM:"+ NUM.get());
                NUM.remove();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        poolExecutor.shutdown();
    }
}
