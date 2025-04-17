package thinking.threadpooldemo.demo.medium;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MediumThreadPoolTest {
    public static void main(String[] args) {
        MediumThreadPool mediumThreadPool = new MediumThreadPool(5, 10, 1000,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5));
        for (int i = 0; i < 20; i++) {
            try{
                int index = i;
                mediumThreadPool.execute(()->{
                    System.out.println("任务"+index+"starting。。。。。。");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("任务"+index+"ending。。。。。。");
                });
            }catch (Exception e){
                System.out.println("任务提交失败"+i);
            }
        }
    }
}
