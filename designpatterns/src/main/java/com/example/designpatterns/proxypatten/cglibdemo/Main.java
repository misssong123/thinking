package com.example.designpatterns.proxypatten.cglibdemo;

public class Main {
    public static void main(String[] args) {
        //TVFactoryA
        TVFactoryA tvFactoryA = new TVProxy<TVFactoryA>().getProxy(TVFactoryA.class);
        System.out.println(tvFactoryA.produceTV());
        tvFactoryA.repairTV();
        System.out.println("------------------------------------------------------------------------------------");
        //TVFactoryB
        TVFactoryB tvFactoryB = new TVProxy<TVFactoryB>().getProxy(TVFactoryB.class);
        System.out.println(tvFactoryB.produceTV());
        tvFactoryB.repairTV();
    }
}
