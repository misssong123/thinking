package thinking.referencedemo;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakReferenceDemo {
    public static void main(String[] args) {
        test2();
    }
    public static void test2() {
        WeakClass[] weakClasses = new WeakClass[10];
        for (int i = 0; i < weakClasses.length; i++) {
             weakClasses[i] = new WeakClass(new WeakDemo());
        }
        System.gc();
        for (int i = 0; i < weakClasses.length; i++) {
            System.out.println(weakClasses[i].get());
            System.out.println(weakClasses[i]);
        }
    }
    /**
     * 1.弱引用会在gc时被回收
     */
    public static void test1() {
        WeakReference weakReference1 = new WeakReference(new byte[1024 * 1024 * 4]);
        WeakReference weakReference2 = new WeakReference(new byte[1024 * 1024 * 4]);
        WeakReference weakReference3 = new WeakReference(new byte[1024 * 1024 * 2]);
        WeakReference weakReference4 = new WeakReference(new byte[1024 * 1024 * 2]);
        System.out.println("weakReference1:"+weakReference1.get());
        System.out.println("weakReference2:"+weakReference2.get());
        System.out.println("weakReference3:"+weakReference3.get());
        System.out.println("weakReference4:"+weakReference4.get());
        byte[] bytes = new byte[1024 * 1024 * 6];
        System.out.println("weakReference1:"+weakReference1.get());
        System.out.println("weakReference2:"+weakReference2.get());
        System.out.println("weakReference3:"+weakReference3.get());
        System.out.println("weakReference4:"+weakReference4.get());
        System.out.println(bytes.length);
    }
}
class WeakClass extends WeakReference<WeakDemo>{

    public WeakClass(WeakDemo referent) {
        super(referent);
    }

    public WeakClass(WeakDemo referent, ReferenceQueue<? super WeakDemo> q) {
        super(referent, q);
    }
}
class WeakDemo{
    private byte[] bytes = new byte[1024 * 1024 * 2];
}