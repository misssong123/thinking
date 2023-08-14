package thinking.proxy.staticproxy;

public class TVCompany implements TVInterFace{
    @Override
    public TV produceTV() {
        System.out.println("TV company produce TV...");
        return new TV("小米电视机","合肥");
    }
}
