package thinking.threadT;

public class ThreadCreateWay {
    public static void main(String[] args) {
        way1();
    }

    /**
     * 线程构造方法
     */
    public static void way1(){
        //无参
        Thread thread1 = new Thread();
        thread1.start();
        //Runnable
        Thread thread2 = new Thread(()-> System.out.println("Runnable"));
        thread2.start();
        //ThreadGroup+Runnable
        ThreadGroup threadGroup = new ThreadGroup("threadGroup");
        Thread thread3 = new Thread(threadGroup,()-> System.out.println("ThreadGroup"));
        thread3.start();
        //String
        Thread thread4 = new Thread("thread4");
        thread4.start();
        //ThreadGroup+String
        Thread thread5 = new Thread(threadGroup,"thread5");
        thread5.start();
        //Runnable+String
        Thread thread6 = new Thread(()-> System.out.println("Runnable+String"),"thread6");
        thread6.start();
        //ThreadGroup+Runnable+String
        Thread thread7 = new Thread(threadGroup,()-> System.out.println("ThreadGroup+Runnable+String"),"thread7");
        thread7.start();
        //ThreadGroup+Runnable+String+long
        Thread thread8 = new Thread(threadGroup,()-> System.out.println("ThreadGroup+Runnable+String+long"),"thread8",10);
        thread8.start();
    }
}
