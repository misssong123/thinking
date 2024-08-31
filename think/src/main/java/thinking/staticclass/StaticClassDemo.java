package thinking.staticclass;

/**
 * 1.静态类
 */
public class StaticClassDemo {
    private static int a = 1;
    private int b = 2;
    private StaticClassDemo(){

    }
    public static StaticClassDemo getStaticClassDemo(){
        return InnerStaticClass.demo;
    }
    private static class InnerStaticClass{
        static StaticClassDemo demo = new StaticClassDemo();
    }

    public static void main(String[] args) {
        StaticClassDemo demo = StaticClassDemo.getStaticClassDemo();
        System.out.println(StaticClassDemo.a);
        System.out.println(demo.b);
    }
}