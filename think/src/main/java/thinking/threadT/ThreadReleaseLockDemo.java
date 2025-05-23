package thinking.threadT;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 线程锁的demo
 */
public class ThreadReleaseLockDemo {
    public static final Object LOCK = new Object();
    public static ThreadReleaseLockDemo demo = new ThreadReleaseLockDemo();
    public static void main(String[] args) {
        //demo.releaseLock();
        //demo.noReleaseLock();
        //demo.joinReleaseLock();
        demo.joinNoReleaseLock();
    }
    //WAITING状态/TIMED_WAITING状态释放锁
    public void releaseLock(){
        Thread thread1 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    System.out.println(Thread.currentThread().getName()+":waiting....");
                    TimeUnit.MILLISECONDS.sleep(2000);
                    LOCK.wait();
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    TimeUnit.MILLISECONDS.sleep(2000);
                    LOCK.notify();
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread2");
        thread1.start();
        thread2.start();
    }
    //TIMED_WAITING状态不释放锁
    //调用 Thread.sleep(long millis)：不会释放锁。Thread.sleep() 是让当前线程暂停执行一段时间，但它不会释放线程持有的任何锁。
    //调用 LockSupport.parkNanos(long nanos) 等：不会释放锁。LockSupport 类提供的方法用于线程的阻塞和唤醒，调用 park 相关方法时，线程不会释放锁。
    //join()方法：join() 仅释放被调用线程对象的锁，不影响其他锁。
    public void noReleaseLock(){
        Thread thread1 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    System.out.println(Thread.currentThread().getName()+":sleeping....");
                    //Thread.sleep(2000);
                    LockSupport.parkNanos(2000000000);
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    TimeUnit.MILLISECONDS.sleep(2000);
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread2");
        thread1.start();
        thread2.start();
    }
    //join() 仅释放被调用线程对象的锁，不影响其他锁。
    public void joinReleaseLock(){
        Thread thread1 = new Thread(() -> {
            synchronized (this){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    System.out.println(Thread.currentThread().getName()+":waiting....");
                    TimeUnit.MILLISECONDS.sleep(2000);
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            synchronized (thread1){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    thread1.start();
                    TimeUnit.MILLISECONDS.sleep(2000);
                    thread1.join();
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread2");
        thread2.start();
    }
    public void joinNoReleaseLock(){
        Thread thread1 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    System.out.println(Thread.currentThread().getName()+":waiting....");
                    TimeUnit.MILLISECONDS.sleep(2000);
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            synchronized (LOCK){
                try{
                    //获取到锁
                    System.out.println(Thread.currentThread().getName()+":获取到锁");
                    thread1.start();
                    TimeUnit.MILLISECONDS.sleep(2000);
                    thread1.join();
                    System.out.println(Thread.currentThread().getName()+":任务完成");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "thread2");
        thread2.start();
    }
}
