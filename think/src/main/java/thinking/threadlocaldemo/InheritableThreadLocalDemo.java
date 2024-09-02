package thinking.threadlocaldemo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InheritableThreadLocalDemo {
    static InheritableThreadLocal<Integer> NUM = new InheritableThreadLocal<>();
    public static void test1(){
        Thread thread1 = new Thread(()->{
            NUM.set(NUM.get()+1);
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
            Thread thread3 = new Thread(()->{
                NUM.set(NUM.get()+1);
                System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
                //移除
                NUM.remove();
            },"thread3");
            thread3.start();
            //移除
            NUM.remove();
        },"thread1");
        thread1.start();
        Thread thread2 = new Thread(()->{
            NUM.set(NUM.get()+1);
            System.out.println("线程:"+Thread.currentThread().getName()+";NUM:"+NUM.get());
            //移除
            NUM.remove();
        },"thread2");
        thread2.start();
    }
    public static void main(String[] args) {
        NUM.set(1);
        test2();
    }
    public static void test2(){
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,2,10L,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(3000));
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
