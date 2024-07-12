package com.example.designpatterns.proxypatten.dynamicproxy;

public class TVCompany implements TVInterFace {
    @Override
    public TV produceTV() {
        System.out.println("TV company produce TV...");
        return new TV("小米电视机","合肥");
    }

    @Override
    public void repairTV() {
        System.out.println("TV company repair TV...");
        System.out.println("TV company repair TV 【DONE】");
    }
}
