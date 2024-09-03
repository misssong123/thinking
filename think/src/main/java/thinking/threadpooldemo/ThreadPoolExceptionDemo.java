package thinking.threadpooldemo;

import java.text.SimpleDateFormat;
import java.util.concurrent.*;

/**
 * 线程池异常处理
 * 1.execute 会返回所报的异常
 * 2.submit 会返回一个Future对象，通过Future.get()方法获取异常信息
 * 3.重写afterExecute方法，在方法中处理异常信息
 */
public class ThreadPoolExceptionDemo {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(1,1,10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.DiscardPolicy()) {
        @Override
        protected void afterExecute(Runnable runnable, Throwable throwable) {
            //execute运行
            if (throwable != null) {
                System.out.println(throwable);
            }
            //submit运行
            if (throwable == null && runnable instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) runnable;
                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    throwable = ce;
                    ce.printStackTrace();
                } catch (ExecutionException ee) {
                    throwable = ee.getCause();
                    ee.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    };
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws Exception {
        testExecute();
        testSubmit();
        executor.shutdown();
    }
    public static void testExecute() {
        executor.execute(() -> {
            int i = 1 / 0;
        });
    }
    public static void testSubmit() throws Exception{
        Future<?> submit = executor.submit(() -> {
            int i = 1 / 0;
        });
    }

}
