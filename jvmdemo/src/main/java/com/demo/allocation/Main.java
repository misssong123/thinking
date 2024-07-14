package com.demo.allocation;

/**
 * 1.对象优先在Eden分配
 * 2.大对象直接进入老年代
 * 3.长期存活的对象将进入老年代
 * 4.动态对象年龄判定
 * 5.空间分配担保
 */
public class Main {
    private static final int _1MB = 1024 * 1024;
    public static void main(String[] args) {
        testAllocation5();
    }
    //对象优先在Eden分配
    //-Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    public static void testAllocation1(){
        byte[] allocation1,allocation2,allocation3,allocation4;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[4 * _1MB]; //出现一次Minor GC
    }
    //大对象直接进入老年代
    //-XX:PretenureSizeThreshold=3145728 仅对Serial和ParNew两款收集器有效
    //-Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
    public static void testAllocation2(){
        byte[] allocation1;
        allocation1 = new byte[9 * _1MB];
    }
    //长期存活的对象将进入老年代
    //-XX:MaxTenuringThreshold=15 -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    public static void testAllocation3(){
        byte[] allocation1,allocation2,allocation3;
        allocation1 = new byte[ _1MB /4];
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }
    //动态对象年龄判定
    //-XX:MaxTenuringThreshold=15 -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+PrintTenuringDistribution
    public static void testAllocation4(){
        byte[] allocation1,allocation2,allocation3,allocation4;
        allocation1 = new byte[ _1MB /4];
        allocation2 = new byte[ _1MB /4];
        allocation3 = new byte[4 * _1MB];
        allocation4 = new byte[4 * _1MB];
        allocation4 = null;
        allocation4 = new byte[4 * _1MB];
    }
    //空间分配担保
    //-XX:HandlePromotionFailure -XX:MaxTenuringThreshold=15 -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
    public static void testAllocation5(){
        byte[] allocation1,allocation2,allocation3,allocation4,allocation5,allocation6,allocation7;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation1 = null;
        allocation4 = new byte[2 * _1MB];
        allocation5 = new byte[2 * _1MB];
        allocation6 = new byte[2 * _1MB];
        allocation4 =  null;
        allocation5 = null;
        allocation6 = null;
        allocation7 = new byte[2 * _1MB];
    }
}
