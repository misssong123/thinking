package thinking.proxy.cglibdemo;
public class TVFactoryB {
    public TV produceTV() {
        System.out.println("TVFactoryB produce TV...");
        return new TV("华为电视机","北京");
    }

    public void repairTV() {
        System.out.println("TVFactoryB repair TV...");
        System.out.println("TVFactoryB repair TV 【DONE】");
    }
}
