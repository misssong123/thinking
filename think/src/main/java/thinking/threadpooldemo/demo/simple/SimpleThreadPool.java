package thinking.threadpooldemo.demo.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SimpleThreadPool {
    private List<Worker> workers = new ArrayList<>();
    private BlockingQueue<Runnable> taskQueue;
    public SimpleThreadPool(int poolSize, BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
        for(int i = 0 ; i < poolSize; i++){
            Worker worker = new Worker();
            workers.add(worker);
            worker.start();
        }
    }
    public void execute(Runnable task){
        try{
            //提交任务
            taskQueue.put(task);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    class Worker extends Thread{
        @Override
        public void run() {
            while (true) {
                try{
                    //获取任务
                    Runnable runnable = taskQueue.take();
                    runnable.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
