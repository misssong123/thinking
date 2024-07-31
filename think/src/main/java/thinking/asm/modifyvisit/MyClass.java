package thinking.asm.modifyvisit;

public class MyClass {
    private int age;
    private String name;
    public void myMethod1() {
        // 方法体
        System.out.println("调用了 myMethod1 方法");
    }
    public void myMethod2() {
        // 方法体
        System.out.println("调用了 myMethod2 方法");
    }
    public static void main(String[] args) {
        MyClass demo = new MyClass();
        demo.myMethod1();
    }
}
