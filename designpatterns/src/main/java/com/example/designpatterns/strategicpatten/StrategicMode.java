package com.example.designpatterns.strategicpatten;

public class StrategicMode {
    //聚合策略类对象
    private Strategy strategy;

    public StrategicMode(Strategy strategy){
        this.strategy = strategy;
    }

    //向客户展示促销活动
    public void salesManShow(){
        strategy.show();
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
