package thinking.proxy.staticproxy;

import java.util.Objects;

public class TVProxy implements TVInterFace{

    private TVCompany tvCompany;

    public TVProxy() {
    }

    @Override
    public TV produceTV() {
        System.out.println("TV proxy get order .... ");
        System.out.println("TV proxy start produce .... ");
        if(Objects.isNull(tvCompany)){
            System.out.println("machine proxy find factory .... ");
            tvCompany = new TVCompany();
        }
        return tvCompany.produceTV();
    }
}
