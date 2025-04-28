package thinking.queuedemo;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        //测试System.arraycopy方法
        Integer[] elements = {1,2,3,4, 5,6,7,8};
        int head = 4;
        int tail = 4;
        /**
         * int p = head;
         *int n = elements.length;
         *int r = n - p; // number of elements to the right of p
         * int newCapacity = n << 1;
         *         Object[] a = new Object[newCapacity];
         *         System.arraycopy(elements, p, a, 0, r);
         *         System.arraycopy(elements, 0, a, r, p);
         *         elements = a;
         *         head = 0;
         *         tail = n;
         */
        Integer[] a = new Integer[16];
        int p = 4;
        int n = 8;
        int r = n - p;
        System.arraycopy(elements, p, a, 0, r);
        System.out.println(Arrays.toString(a));
        System.arraycopy(elements, 0, a, r, p);
        System.out.println(Arrays.toString(a));
    }
}
