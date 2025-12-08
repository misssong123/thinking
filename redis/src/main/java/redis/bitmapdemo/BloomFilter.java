package redis.bitmapdemo;

import redis.clients.jedis.Jedis;

/**
 * 布隆过滤器实现
 */
public class BloomFilter {
    private Jedis jedis;
    private String key;
    private int size;           // 位图大小
    private int hashFunctions;  // 哈希函数数量

    public BloomFilter(Jedis jedis, String key, int expectedInsertions, double falsePositiveRate) {
        this.jedis = jedis;
        this.key = key;

        // 计算最优的位图大小和哈希函数数量
        this.size = optimalNumOfBits(expectedInsertions, falsePositiveRate);
        System.out.println("元素预期插入「"+expectedInsertions+"」误判率位图大小: 「" + size+"」");
        this.hashFunctions = optimalNumOfHashFunctions(expectedInsertions, size);
        System.out.println("元素预期插入「"+expectedInsertions+"」误判率哈希函数数量: 「" + hashFunctions+"」");
    }

    /**
     * 添加元素
     */
    public void add(String value) {
        for (int i = 0; i < hashFunctions; i++) {
            long hash = hash(value, i) % size;
            jedis.setbit(key, hash, true);
        }
    }

    /**
     * 检查元素是否存在（可能存在误判）
     */
    public boolean mightContain(String value) {
        for (int i = 0; i < hashFunctions; i++) {
            long hash = hash(value, i) % size;
            if (!jedis.getbit(key, hash)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算布隆过滤器的误判率
     */
    public double getFalsePositiveRate(int insertedCount) {
        if (insertedCount <= 0) {
            return 0.0;
        }

        // 计算公式: (1 - e^(-k * n / m)) ^ k
        double exponent = -hashFunctions * (double) insertedCount / size;
        double base = 1 - Math.exp(exponent);
        return Math.pow(base, hashFunctions);
    }

    /**
     * 获取当前已设置的位数
     */
    public Long getBitsSet() {
        return jedis.bitcount(key);
    }

    /**
     * 计算最佳位数组大小
     */
    private int optimalNumOfBits(int n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算最佳哈希函数数量
     */
    private int optimalNumOfHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * 哈希函数（模拟多个哈希函数）
     */
    private long hash(String value, int seed) {
        long result = 0x811c9dc5L; // FNV偏移基础值

        byte[] bytes = value.getBytes();
        for (byte b : bytes) {
            result ^= (b + seed);
            result *= 0x01000193L; // FNV质数
        }

        // 确保为正数
        return result & 0x7fffffffL;
    }
}
