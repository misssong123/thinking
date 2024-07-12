package com.example.designpatterns.proxypatten.dynamicproxy;

public class Main {
    public static void main(String[] args) {
        TVCompany tvCompany = new TVCompany();
        TVInterFace proxy = (TVInterFace) new TVProxy(tvCompany).getProxy();
        System.out.println(proxy.produceTV());
        proxy.repairTV();
    }
}
