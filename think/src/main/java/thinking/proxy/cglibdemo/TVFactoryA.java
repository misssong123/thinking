package thinking.proxy.cglibdemo;

public class TVFactoryA {
    public TV produceTV() {
        System.out.println("TVFactoryA produce TV...");
        return new TV("小米电视机","合肥");
    }

    public void repairTV() {
        System.out.println("TVFactoryA repair TV...");
        System.out.println("TVFactoryA repair TV 【DONE】");
    }
}
