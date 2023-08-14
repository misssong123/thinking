package thinking.proxy.dynamicproxy;

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
