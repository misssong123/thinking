package com.example.designpatterns.proxypatten.dynamicproxy;

public interface TVInterFace {
    /**
     * 生产电视
     * @return
     */
    TV produceTV();
    /**
     * 维修电视
     */
    void repairTV();
}
