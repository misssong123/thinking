package thinking.threadlocaldemo;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadLocalDemo {
    /**
     * 声明方式
     * 1.ThreadLocal.withInitial(()->0) 可以进行初始化，实现类为SuppliedThreadLocal
     * 2. new ThreadLocal<>();不可以进行初始化
     */
    static ThreadLocal<Integer> NUM = ThreadLocal.withInitial(()->0);
    public static void test2(){
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,2,10L,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(3000));
        for(int i = 0 ; i < 6; i++){
            poolExecutor.execute(() -> {
                if (NUM.get() == null){
                    NUM.set(0);
                }
                NUM.set(NUM.get()+1);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
                //移除
                //NUM.remove();
            });
        }
        poolExecutor.shutdown();
    }
    public static void main(String[] args) {
        test2();
    }
    public static void test1(){
        new Thread(() -> {
            NUM.set(1);
            System.out.println("threadName:"+Thread.currentThread().getName()+" NUM:"+NUM.get());
        }).start();
        new Thread(() -> {
            NUM.set(1);
            System.out.println("threadName:"+Thread.currentThread().getName()+" NUM:"+NUM.get());
        }).start();
    }





}
