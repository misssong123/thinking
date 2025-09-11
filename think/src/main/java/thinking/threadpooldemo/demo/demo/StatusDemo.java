package thinking.threadpooldemo.demo.demo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 状态demo
 */
public class StatusDemo {
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
    private static final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    public static void main(String[] args) {
        System.out.println(format("CAPACITY:")+format(CAPACITY));
        System.out.println(format("CTL:")+format(ctl.get()));
        System.out.println(format("RUNNING:")+format(RUNNING));
        System.out.println(format("SHUTDOWN:")+format(SHUTDOWN));
        System.out.println(format("STOP:")+format(STOP));
        System.out.println(format("TIDYING:")+format(TIDYING));
        System.out.println(format("TERMINATED:")+format(TERMINATED));
        //运行状态
        System.out.println(format("STATUS:")+format(runStateOf(ctl.get())));
        //线程池大小
        System.out.println(format("COUNT:")+format(workerCountOf(ctl.get())));
        //线程数+1
        ctl.compareAndSet(0,1);
        System.out.println("-------------------线程数+1--------------------");
        System.out.println(format("STATUS:")+format(runStateOf(ctl.get())));
        System.out.println(format("COUNT:")+format(workerCountOf(ctl.get())));
    }
    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static String format(int original){
        String binary = Integer.toBinaryString(original);
        return String.format("%32s", binary).replace(' ', '0');
    }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static String format(String original){
        return String.format("%12s", original);
    }
}
