package thinking.threadpooldemo.demo.demo;

import java.util.concurrent.*;

public class ThreadPoolDemo {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(2,5,10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());
    public static void main(String[] args) throws Exception {
        System.out.println(Integer.toBinaryString(-1));
        int COUNT_BITS = Integer.SIZE - 3;
        int RUNNING    = -1 << COUNT_BITS;
        System.out.println(Integer.toBinaryString(RUNNING));
        int ctl = RUNNING | 0;
        System.out.println(Integer.toBinaryString(ctl));
        int SHUTDOWN   =  0 << COUNT_BITS;
        System.out.println(Integer.toBinaryString(SHUTDOWN));

        int STOP       =  1 << COUNT_BITS;
        System.out.println(Integer.toBinaryString(STOP));

        int TIDYING    =  2 << COUNT_BITS;
        System.out.println(Integer.toBinaryString(TIDYING));

        int TERMINATED =  3 << COUNT_BITS;
        System.out.println(Integer.toBinaryString(TERMINATED));

    }

    private static void executeMethod() {
        //1.创建核心为5的线程池
        for(int i = 1 ; i <= 10 ; i ++){
            int finalI = i;
            executor.execute(()->{
                System.out.println(Thread.currentThread().getName()+"("+ finalI +")"+" is running");
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName()+"("+ finalI +")"+" is stopped");
            });
        }
        //调整队列大小
        executor.setMaximumPoolSize(2);
        executor.shutdown();
    }
    private static void submitMethod() {
        //1.创建核心为5的线程池
        for(int i = 1 ; i <= 10 ; i ++){
            int finalI = i;
            executor.submit(()->{
                System.out.println(Thread.currentThread().getName()+"("+ finalI +")"+" is running");
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName()+"("+ finalI +")"+" is stopped");
            });
        }
        //调整队列大小
        executor.setMaximumPoolSize(2);
        executor.shutdown();
    }
}
