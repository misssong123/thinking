package thinking.threadpooldemo.demo.medium;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MediumThreadPool {
    int corePoolSize;
    int maxPoolSize;
    long keepAliveTime;
    BlockingQueue<Runnable> workQueue;
    List<Worker> workers = new ArrayList<>();
    public MediumThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit,
                            BlockingQueue<Runnable> workQueue) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = timeUnit.toNanos(keepAliveTime);
        this.workQueue = workQueue;
    }
    public void execute(Runnable task) {
        if(Objects.isNull(task)) {
            throw new NullPointerException();
        }
        //创建核心线程
        if(workers.size() < corePoolSize){
            addWorker(task,true);
            return;
        }
        //放入队列
        boolean offer = workQueue.offer(task);
        if(offer){
            return;
        }
        //创建非核心线程
        if(workers.size() < maxPoolSize){
            addWorker(task,false);
            return;
        }
        throw new RuntimeException("拒绝策略。。。。。");
    }

    private boolean addWorker(Runnable task, boolean core) {
        if (workers.size() > (core ? corePoolSize : maxPoolSize)) {
            return false;
        }
        Worker worker = new Worker(task);
        worker.thread.start();
        workers.add(worker);
        return true;
    }
    private Runnable getTask() {
        boolean timeOut = false;
        while (true){
            boolean destroy = workers.size() > corePoolSize;
            if (destroy && timeOut){
                return null;
            }
            try{
                Runnable task = destroy ? workQueue.poll(keepAliveTime,TimeUnit.MILLISECONDS)
                        : workQueue.take();
                if (task != null){
                    return task;
                }
                timeOut = true;
            }catch (InterruptedException e){
                e.printStackTrace();
                timeOut = false;
            }
        }
    }
    class Worker implements Runnable {
        private Runnable task;
        private Thread thread;
        public Worker(Runnable task) {
            this.task = task;
            thread = new Thread(this);
        }
        @Override
        public void run() {
            try{
                while (task != null || (task = getTask())!= null){
                    task.run();
                    task = null;
                }
            }finally {
                //被销毁
                System.out.println(this + "被销毁");
                workers.remove(this);
            }
        }
    }


}
