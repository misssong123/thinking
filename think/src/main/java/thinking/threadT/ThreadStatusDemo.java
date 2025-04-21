package thinking.threadT;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class ThreadStatusDemo {
    /**
     * 线程池状态
     * 1.NEW（新建）：当你创建一个 Thread 对象，但还没有调用 start() 方法时，
     *  线程处于新建状态。此时，线程仅仅是一个对象实例，还没有真正开始执行。
     * 2.RUNNABLE（可运行）：调用 start() 方法后，线程进入可运行状态。
     *  这意味着线程已经准备好执行，它可能正在 CPU 上运行，也可能在等待系统分配 CPU 资源。
     * 3.BLOCKED（阻塞）：线程在等待获取一个排他锁，而该锁正在被其他线程持有，此时线程会进入阻塞状态。
     *  当锁被释放并且该线程获得锁后，它将重新进入可运行状态。
     * 4.WAITING（等待）：线程调用了某些方法（如 Object.wait()、Thread.join() 或 LockSupport.park()）后，
     *  会进入等待状态。处于等待状态的线程需要其他线程显式地唤醒（如调用 Object.notify() 或 Object.notifyAll()）才能继续执行。
     * 5.TIMED_WAITING（定时等待）：与等待状态类似，但线程在一定时间后会自动唤醒。例如，调用 Thread.sleep(long millis)、
     *  Object.wait(long timeout) 或 LockSupport.parkNanos(long nanos) 等方法会使线程进入定时等待状态。
     * 6.TERMINATED（终止）：线程的 run() 方法执行完毕，或者因为异常退出了 run() 方法，线程就会进入终止状态。
     *  一旦线程进入终止状态，它就不能再重新启动。
     */
    public static void main(String[] args) throws Exception{
        //1.新建状态
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getState());
        });
        System.out.println(thread.getState());
        //2.运行状态
        thread.start();
        //3.终止状态
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(thread.getState());
        //4.阻塞方法
        ThreadStatusDemo demo = new ThreadStatusDemo();
        Thread thread1 = new Thread(demo::test,"thread1");
        thread1.start();
        Thread thread2 = new Thread(demo::test,"thread2");
        thread2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("thread2:"+thread2.getState());
        //5.WAITING（等待）
        Thread thread3 = new Thread(() -> {
            try {
                thread2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"thread3");
        thread3.start();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("thread3:"+thread3.getState());
        //6.TIMED_WAITING（定时等待）
        Thread thread4 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"thread4");
        thread4.start();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("thread4:"+thread4.getState());
    }

    /**
     * 阻塞方法
     */
    public synchronized void test(){
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
