package thinking.consistenthash;
import thinking.consistenthash.util.MigrationResult;
import thinking.consistenthash.util.StatsCollector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 一致性哈希算法完整实现
 */
public class ConsistentHash<T> {

    // 哈希函数接口
    public interface HashFunction {
        int hash(String key);
    }

    // MD5哈希函数（分布均匀）
    public static class MD5Hash implements HashFunction {
        @Override
        public int hash(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(key.getBytes());
                byte[] digest = md.digest();
                int result = 0;
                for (int i = 0; i < 4; i++) {
                    result = (result << 8) | (digest[i] & 0xFF);
                }
                return result & 0x7FFFFFFF;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5算法不可用", e);
            }
        }
    }

    // FNV1哈希函数（高性能）
    public static class FNV1Hash implements HashFunction {
        private static final int FNV1_32_INIT = 0x811c9dc5;
        private static final int FNV1_PRIME_32 = 0x01000193;

        @Override
        public int hash(String key) {
            int hash = FNV1_32_INIT;
            byte[] bytes = key.getBytes();
            for (byte b : bytes) {
                hash = (hash * FNV1_PRIME_32) ^ (b & 0xff);
            }
            return hash & 0x7FFFFFFF;
        }
    }

    // MurmurHash（高性能，低碰撞）
    public static class MurmurHash implements HashFunction {
        @Override
        public int hash(String key) {
            byte[] bytes = key.getBytes();
            return murmurHash32(bytes, bytes.length, 0x12345678);
        }

        private int murmurHash32(byte[] data, int length, int seed) {
            int m = 0x5bd1e995;
            int r = 24;

            int h = seed ^ length;
            int i = 0;

            while (length >= 4) {
                int k = data[i] & 0xFF;
                k |= (data[i + 1] & 0xFF) << 8;
                k |= (data[i + 2] & 0xFF) << 16;
                k |= (data[i + 3] & 0xFF) << 24;

                k *= m;
                k ^= k >>> r;
                k *= m;

                h *= m;
                h ^= k;

                i += 4;
                length -= 4;
            }

            switch (length) {
                case 3: h ^= (data[i + 2] & 0xFF) << 16;
                case 2: h ^= (data[i + 1] & 0xFF) << 8;
                case 1: h ^= data[i] & 0xFF;
                    h *= m;
            }

            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h & 0x7FFFFFFF;
        }
    }

    // 哈希环存储结构
    public final SortedMap<Integer, VirtualNode<T>> circle = new TreeMap<>();
    public final HashFunction hashFunction;
    private final int virtualNodesPerPhysicalNode;

    // 物理节点到虚拟节点的映射
    public final Map<T, List<VirtualNode<T>>> physicalToVirtual = new HashMap<>();

    // 统计信息
    private final StatsCollector stats = new StatsCollector();

    // 构造函数
    public ConsistentHash(HashFunction hashFunction, int virtualNodesPerPhysicalNode) {
        this.hashFunction = hashFunction;
        this.virtualNodesPerPhysicalNode = virtualNodesPerPhysicalNode;
    }

    public ConsistentHash(int virtualNodesPerPhysicalNode) {
        this(new FNV1Hash(), virtualNodesPerPhysicalNode);
    }

    /**
     * 添加物理节点
     */
    public synchronized void addPhysicalNode(T node) {
        if (physicalToVirtual.containsKey(node)) {
            System.out.println("节点 " + node + " 已存在");
            return;
        }

        System.out.println("\n[添加节点] " + node);
        List<VirtualNode<T>> virtualNodes = new ArrayList<>();

        // 创建虚拟节点
        for (int i = 0; i < virtualNodesPerPhysicalNode; i++) {
            String virtualKey = node.toString() + "#VN" + i;
            int hash = hashFunction.hash(virtualKey);

            VirtualNode<T> vNode = new VirtualNode<>(node, "VN" + i, hash);
            virtualNodes.add(vNode);

            // 检查是否有冲突
            if (circle.containsKey(hash)) {
                System.out.println("警告: 虚拟节点哈希冲突: " + vNode);
                // 解决冲突: 添加后缀重新哈希
                String newKey = virtualKey + "_" + System.currentTimeMillis();
                hash = hashFunction.hash(newKey);
                vNode = new VirtualNode<>(node, "VN" + i, hash);
            }

            circle.put(hash, vNode);
            System.out.println("  添加虚拟节点: " + vNode + " (hash: " + hash + ")");
        }

        physicalToVirtual.put(node, virtualNodes);
        stats.recordRequest();
        // 输出节点分布
        printNodeDistribution();
    }

    /**
     * 移除物理节点
     */
    public synchronized void removePhysicalNode(T node) {
        if (!physicalToVirtual.containsKey(node)) {
            System.out.println("节点 " + node + " 不存在");
            return;
        }

        System.out.println("\n[移除节点] " + node);
        List<VirtualNode<T>> virtualNodes = physicalToVirtual.get(node);

        // 移除所有虚拟节点
        for (VirtualNode<T> vNode : virtualNodes) {
            circle.remove(vNode.hash);
            System.out.println("  移除虚拟节点: " + vNode);
        }

        physicalToVirtual.remove(node);
        stats.recordRequest();

        // 输出节点分布
        printNodeDistribution();
    }

    /**
     * 获取数据对应的节点
     */
    public synchronized T getNode(String key) {
        if (circle.isEmpty()) {
            return null;
        }

        int hash = hashFunction.hash(key);

        // 在环上查找
        SortedMap<Integer, VirtualNode<T>> tailMap = circle.tailMap(hash);
        Integer nodeHash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();

        VirtualNode<T> vNode = circle.get(nodeHash);
        return vNode.physicalNode;
    }

    /**
     * 获取数据对应的节点（带迁移检测）
     */
    public synchronized MigrationResult<T> getNodeWithMigrationCheck(String key, T previousNode) {
        T currentNode = getNode(key);

        if (previousNode != null && !previousNode.equals(currentNode)) {
            stats.recordMigration(previousNode.toString(), currentNode.toString());
            return new MigrationResult<T>(currentNode, true, previousNode, currentNode);
        }

        return new MigrationResult<T>(currentNode, false, previousNode, currentNode);
    }


    /**
     * 获取所有物理节点
     */
    public Set<T> getPhysicalNodes() {
        return new HashSet<>(physicalToVirtual.keySet());
    }

    /**
     * 获取虚拟节点数量
     */
    public int getVirtualNodeCount() {
        return circle.size();
    }

    /**
     * 打印节点分布
     */
    public void printNodeDistribution() {
        System.out.println("\n当前节点分布:");
        System.out.println("物理节点数: " + physicalToVirtual.size());
        System.out.println("虚拟节点数: " + circle.size());

        Map<T, Integer> distribution = new HashMap<>();
        for (VirtualNode<T> vNode : circle.values()) {
            distribution.put(vNode.physicalNode,
                    distribution.getOrDefault(vNode.physicalNode, 0) + 1);
        }

        distribution.forEach((node, count) ->
                System.out.println("  " + node + ": " + count + "个虚拟节点")
        );
    }

    /**
     * 获取统计器
     */
    public StatsCollector getStats() {
        return stats;
    }

    /**
     * 打印哈希环详情
     */
    public void printRingDetails() {
        System.out.println("\n=== 哈希环详情 ===");
        System.out.println("哈希环大小: " + circle.size());

        List<Map.Entry<Integer, VirtualNode<T>>> entries = new ArrayList<>(circle.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            Map.Entry<Integer, VirtualNode<T>> entry = entries.get(i);
            System.out.printf("  %10d -> %s\n", entry.getKey(), entry.getValue());
        }

        if (entries.size() > 10) {
            System.out.println("  ... (共" + entries.size() + "个虚拟节点)");
        }
    }
}
