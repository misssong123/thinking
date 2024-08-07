package thinking.threadlocaldemo;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadLocalDemo {
    static ThreadLocal<Integer> NUM = ThreadLocal.withInitial(()->0);
    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,2,10L,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(3000));
        NUM.set(1);
        for(int i = 0 ; i < 10 ; i++){
            poolExecutor.execute(() -> {
                NUM.set(NUM.get()+1);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
                //移除
                NUM.remove();
            });
        }
        poolExecutor.shutdown();
    }
}
