package thinking.threadlocaldemo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InheritableThreadLocalDemo {
    static ThreadLocal<Integer> NUM = new InheritableThreadLocal<>();
    static ThreadLocal<Integer> NUM1 = InheritableThreadLocal.withInitial(() -> 0);
    public static void main(String[] args) {
        NUM.set(1);
        NUM1.set(1);
//        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,2,10L,
//                TimeUnit.SECONDS,new ArrayBlockingQueue<>(3000));
//        for(int i = 0 ; i < 10 ; i++){
//            poolExecutor.execute(() -> {
//                NUM.set(NUM.get()+1);
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
//                //移除
//                NUM.remove();
//            });
//        }
//        poolExecutor.shutdown();
        new Thread(()->{
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get()+";NUM1:"+NUM1.get());
            NUM.set(NUM.get()+1);
            NUM1.set(NUM1.get()+1);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get()+";NUM1:"+NUM1.get());
            //移除
            NUM.remove();
            NUM1.remove();
        }).start();
        new Thread(()->{
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get()+";NUM1:"+NUM1.get());
            NUM.set(NUM.get()+1);
            NUM1.set(NUM1.get()+1);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get()+";NUM1:"+NUM1.get());
            //移除
            NUM.remove();
            NUM1.remove();
        }).start();
    }

}
