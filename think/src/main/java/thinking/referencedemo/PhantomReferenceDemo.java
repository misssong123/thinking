package thinking.referencedemo;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceDemo {
    private static class MyObject {
        private String name;

        public MyObject(String name) {
            this.name = name;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("Finalizing " + name);
            super.finalize();
        }
    }

    public static void main(String[] args) {
        ReferenceQueue<MyObject> queue = new ReferenceQueue<>();

        MyObject obj = new MyObject("Test Object");
        PhantomReference<MyObject> phantomRef = new PhantomReference<>(obj, queue);

        // 强制对象被垃圾回收器回收
        obj = null; // 断开强引用
        System.gc(); // 请求垃圾回收

        try {
            Thread.sleep(1000); // 等待垃圾回收完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检查引用队列
        if (queue.poll() != null) {
            System.out.println("Phantom reference added to queue.");
        }

        // 清理资源
        System.out.println("Performing cleanup tasks...");
    }
}
