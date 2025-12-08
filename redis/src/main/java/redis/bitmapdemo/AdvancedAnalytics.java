package redis.bitmapdemo;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AdvancedAnalytics {
    private Jedis jedis;

    public AdvancedAnalytics(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 精确统计活跃用户（Bitmap） + 基数估计（HyperLogLog）
     */
    public Map<String, Object> getAdvancedUserStats(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();

        String bitmapKey = "dau:bitmap:" + date.format(DateTimeFormatter.ISO_DATE);
        String hllKey = "dau:hll:" + date.format(DateTimeFormatter.ISO_DATE);

        // Bitmap精确计数
        Long exactCount = jedis.bitcount(bitmapKey);
        stats.put("exact_dau", exactCount);

        // HyperLogLog估计
        Long estimatedCount = jedis.pfcount(hllKey);
        stats.put("estimated_dau", estimatedCount);

        // 计算误差率
        if (exactCount > 0) {
            double errorRate = Math.abs(exactCount - estimatedCount) / (double) exactCount * 100;
            stats.put("error_rate_percent", String.format("%.4f%%", errorRate));
        }

        return stats;
    }

    /**
     * 组合Bitmap和HyperLogLog进行用户行为分析
     */
    public void recordUserAction(String userId, String action, LocalDate date) {
        // 使用Bitmap记录精确用户
        String bitmapKey = "action:" + action + ":bitmap:" + date.format(DateTimeFormatter.ISO_DATE);
        long userIdHash = Math.abs(userId.hashCode()) % Integer.MAX_VALUE;
        jedis.setbit(bitmapKey, userIdHash, true);

        // 使用HyperLogLog进行基数估计
        String hllKey = "action:" + action + ":hll:" + date.format(DateTimeFormatter.ISO_DATE);
        jedis.pfadd(hllKey, userId);

        // 设置过期时间
        jedis.expire(bitmapKey, 30 * 24 * 3600);
        jedis.expire(hllKey, 30 * 24 * 3600);
    }
}
