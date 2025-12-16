package thinking.consistenthash;

import java.util.*;

/**
 * 带权重的一致性Hash算法
 */
public class WeightedConsistentHash<T> extends ConsistentHash<T> {

    // 节点权重映射
    private final Map<T, Integer> nodeWeights = new HashMap<>();
    private final int baseVirtualNodes;

    public WeightedConsistentHash(HashFunction hashFunction, int baseVirtualNodes) {
        super(hashFunction, baseVirtualNodes);
        this.baseVirtualNodes = baseVirtualNodes;
    }

    /**
     * 添加带权重的节点
     */
    public void addWeightedNode(T node, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("权重必须大于0: " + weight);
        }

        nodeWeights.put(node, weight);

        // 计算虚拟节点数量
        int virtualNodes = baseVirtualNodes * weight;

        // 添加节点（重写父类方法逻辑）
        addNodeWithVirtualCount(node, virtualNodes);
    }

    /**
     * 根据权重添加节点
     */
    private synchronized void addNodeWithVirtualCount(T node, int virtualCount) {
        System.out.println("添加权重节点: " + node +
                " (权重: " + nodeWeights.get(node) +
                ", 虚拟节点: " + virtualCount + ")");

        List<VirtualNode<T>> virtualNodes = new ArrayList<>();

        for (int i = 0; i < virtualCount; i++) {
            String virtualKey = node.toString() + "#W" + i;
            int hash = hashFunction.hash(virtualKey);

            VirtualNode<T> vNode = new VirtualNode<>(node, "W" + i, hash);
            virtualNodes.add(vNode);

            // 添加到环
            circle.put(hash, vNode);
        }

        physicalToVirtual.put(node, virtualNodes);
    }

    /**
     * 更新节点权重
     */
    public void updateNodeWeight(T node, int newWeight) {
        if (!nodeWeights.containsKey(node)) {
            throw new IllegalArgumentException("节点不存在: " + node);
        }

        // 移除旧节点
        removePhysicalNode(node);

        // 添加新权重节点
        addWeightedNode(node, newWeight);

        System.out.println("更新节点权重: " + node + " -> " + newWeight);
    }

    /**
     * 获取节点权重
     */
    public int getNodeWeight(T node) {
        return nodeWeights.getOrDefault(node, 0);
    }

    /**
     * 打印权重分布
     */
    public void printWeightDistribution() {
        System.out.println("\n=== 权重分布 ===");

        for (Map.Entry<T, Integer> entry : nodeWeights.entrySet()) {
            T node = entry.getKey();
            int weight = entry.getValue();
            int virtualCount = physicalToVirtual.getOrDefault(node, Collections.emptyList()).size();

            System.out.printf("  %s: 权重=%d, 虚拟节点=%d\n",
                    node, weight, virtualCount);
        }
    }
}
