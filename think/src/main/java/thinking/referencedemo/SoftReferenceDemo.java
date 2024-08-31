package thinking.referencedemo;

import lombok.Getter;
import lombok.Setter;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 *1.当前内存空间不足时，会回收部分软引用的数据
 * 2.软引用 (SoftReference) 指向的是对象的引用地址，而不是直接指向实际的堆内存地址。
 * 3.堆内存已经接近或超过了最大限制，但软引用对象在内存压力还不是特别大的情况下，可以暂时保留下来。
 */
public class SoftReferenceDemo {
    public static void main(String[] args) throws Exception{
        test3();
    }
    private static void test3() throws Exception{
        byte[] bytes1 = new byte[1024 * 1024 * 2];
        byte[] bytes2 = new byte[1024 * 1024 * 2];
        byte[] bytes3 = new byte[1024 * 1024 * 2];
        byte[] bytes4 = new byte[1024 * 1024 * 2];
        byte[] bytes5 = new byte[1024 * 1024 * 2];
        byte[] bytes6 = new byte[1024 * 1024 * 2];
        System.out.println(bytes1.length);
        System.out.println(bytes2.length);
        System.out.println(bytes3.length);
        System.out.println(bytes4.length);
        System.out.println(bytes5.length);
        System.out.println(bytes6.length);
    }
    public static void test2() throws Exception{
        SoftClass softClass1 = new SoftClass(new byte[1024 * 1024 * 2]);
        SoftClass softClass2 = new SoftClass(new byte[1024 * 1024 * 2]);
        SoftClass softClass3 = new SoftClass(new byte[1024 * 1024 * 2]);
        SoftClass softClass4 = new SoftClass(new byte[1024 * 1024 * 2]);
        SoftClass softClass5 = new SoftClass(new byte[1024 * 1024 * 2]);
        byte[] bytes = new byte[1024 * 1024 * 2];
        System.out.println(bytes.length);
        System.out.println(softClass1.getBytes().length);
        System.out.println(softClass2.getBytes().length);
        System.out.println(softClass3.getBytes().length);
        System.out.println(softClass4.getBytes().length);
        System.out.println(softClass5.getBytes().length);
        System.out.println(bytes.length);
        System.out.println(softClass1.getBytes().length);
        System.out.println(softClass2.getBytes().length);
        System.out.println(softClass3.getBytes().length);
        System.out.println(softClass4.getBytes().length);
        System.out.println(softClass5.getBytes().length);

    }
    public static void test1() throws Exception{
        byte[] bytes = new byte[1024 * 1024 * 2];
        SoftReference<byte[]> softReference1 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        SoftReference<byte[]> softReference2 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        SoftReference<byte[]> softReference3 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        SoftReference<byte[]> softReference4 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        SoftReference<byte[]> softReference5 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        System.out.println(bytes.length);
        System.out.println(softReference1.get());
        System.out.println(softReference2.get());
        System.out.println(softReference3.get());
        System.out.println(softReference4.get());
        System.out.println(softReference5.get());
        System.out.println("--------------第五次--------------------");
        SoftReference<byte[]> softReference6 = new SoftReference<>(new byte[1024 * 1024 * 2]);
        System.out.println(softReference1.get());
        System.out.println(softReference2.get());
        System.out.println(softReference3.get());
        System.out.println(softReference4.get());
        System.out.println(softReference5.get());
        System.out.println(softReference6.get());
        System.out.println("--------------第六次--------------------");
        System.out.println(bytes.length);
        System.out.println(softReference1.get());
        System.out.println(softReference2.get());
        System.out.println(softReference3.get());
        System.out.println(softReference4.get());
        System.out.println(softReference5.get());
        System.out.println(softReference6.get());
        System.out.println("--------------最总结果--------------------");
    }
}
@Setter
@Getter
class SoftClass extends SoftReference<byte[]>{
    private byte[] bytes = new byte[1024 * 1024 *2];
    public SoftClass(byte[] referent, ReferenceQueue<? super byte[]> q) {
        super(referent, q);
    }

    public SoftClass(byte[] referent) {
        super(referent);
    }
}