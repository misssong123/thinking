package thinking.threadpooldemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 1.shutdown
 * 关闭线程池，拒绝新任务加入，线程池会把正在执行的任务及队列中等待执行的任务都执行完毕后，再去关闭
 * 2.shutdownNow,
 * 正在执行任务的线程会被中断,不一定就不执行；队列中正在排队的任务，会返回
 * 3.awaitTermination
 * 判断在等待的时间内，线程池是否彻底停止
 */
public class ThreadPoolShutDownDemo {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(1,2,10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.DiscardPolicy());
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws Exception{
        testAwaitTermination();
    }
    public static void finalShutdownAndAwaitTermination(ExecutorService threadPool) throws Exception{
        if (Objects.nonNull(threadPool)&&!threadPool.isShutdown()){
            //关闭线程池，拒绝新任务加入，线程池会把正在执行的任务及队列中等待执行的任务都执行完毕后，再去关闭
            threadPool.shutdown();
            try{
                //等待指定时间后，线程线程池还未关闭
                if (!threadPool.awaitTermination(10,TimeUnit.SECONDS)){
                    //强制关闭线程池,正在执行任务的线程会被中断,不一定就不执行；队列中正在排队的任务，会返回
                    threadPool.shutdownNow();
                    if (!threadPool.awaitTermination(10,TimeUnit.SECONDS)){
                        System.out.println("线程池关闭失败");
                    }
                }
            }catch (Exception e){
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

    }
    public static void testAwaitTermination() throws Exception{
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            executor.execute(()-> {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName()+":Num:"+ finalI);
            });
        }
        System.out.println("------------任务提交完成---------"+":"+sdf.format(new Date()));
        executor.awaitTermination(1,TimeUnit.SECONDS);
        System.out.println("------------awaitTermination---------"+":"+sdf.format(new Date()));
        System.out.println(executor.isShutdown());
        System.out.println(executor.isTerminated());
    }
    public static void testShutdown() throws Exception{
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            executor.execute(()-> {

                System.out.println(Thread.currentThread().getName()+":Num:"+ finalI);
            });
        }
        TimeUnit.SECONDS.sleep(1);
        executor.shutdown();
        executor.execute(()-> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("-----"+Thread.currentThread().getName());
        });
    }
    public static void testShutdownNow() throws Exception{
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executor.execute(()-> {
                System.out.println(Thread.currentThread().getName()+":time:"+ sdf.format(new Date())+":Num:"+ finalI);
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("------------任务提交完成---------"+":"+sdf.format(new Date()));
        TimeUnit.SECONDS.sleep(1);
        System.out.println("------------休眠结束---------"+":"+sdf.format(new Date()));
        executor.shutdownNow();
        System.out.println("------------shutdownNow---------"+":"+sdf.format(new Date()));
    }
}
