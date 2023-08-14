package thinking.proxy.staticproxy;

public class Main {
    public static void main(String[] args) {
        TVInterFace tvInterFace = new TVProxy();
        TV tv = tvInterFace.produceTV();
        System.out.println(tv);
    }
}
