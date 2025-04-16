package thinking.threadpooldemo.demo.simple;

import java.util.concurrent.ArrayBlockingQueue;

public class SimpleThreadPoolTest {
    public static void main(String[] args) {
        SimpleThreadPool simpleThreadPool = new SimpleThreadPool(1, new ArrayBlockingQueue<>(2));
        for (int i = 0 ; i < 5 ; i++){
            int index = i;
            simpleThreadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " is running " + index);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " is stopped " + index);
            });
            System.out.println("提交任务" + index + "完成");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
