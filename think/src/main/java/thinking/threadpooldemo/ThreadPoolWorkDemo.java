package thinking.threadpooldemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolWorkDemo {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(2,5,10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50),
            new ThreadPoolExecutor.DiscardPolicy());
    static Random random = new Random();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws Exception {
        for(int i = 0 ; i < 60 ; i++){
            executor.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread monitor = new Thread(()->{
            while (true){
                String sb = "线程池信息：" +
                        "【时间】:" + sdf.format(new Date()) +
                        "【活动线程】:" + executor.getActiveCount() +
                        "【核心线程】:" + executor.getCorePoolSize() +
                        "【最大线程】:" + executor.getMaximumPoolSize() +
                        "【线程池大小】:" + executor.getPoolSize() +
                        "【队列大小】:" + executor.getQueue().size() +
                        "【任务总数】:" + executor.getTaskCount() +
                        "【完成任务数】:" + executor.getCompletedTaskCount();
                System.out.println(sb);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        monitor.setDaemon(true);
        monitor.start();
        TimeUnit.SECONDS.sleep(10);
    }
}
