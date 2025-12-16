package thinking.consistenthash.util;

import java.util.HashMap;
import java.util.Map;
/**
 * 统计收集器
 */
public class StatsCollector {
    private int totalRequests = 0;
    private int migrationCount = 0;
    private final Map<String, Integer> migrationStats = new HashMap<>();

    public void recordRequest() {
        totalRequests++;
    }

    public void recordMigration(String from, String to) {
        migrationCount++;
        String key = from + "->" + to;
        migrationStats.put(key, migrationStats.getOrDefault(key, 0) + 1);
    }

    public void printStats() {
        System.out.println("\n=== 统计信息 ===");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("总迁移次数: " + migrationCount);
        System.out.println("迁移率: " +
                (totalRequests > 0 ? String.format("%.2f%%", migrationCount * 100.0 / totalRequests) : "0%"));

        if (!migrationStats.isEmpty()) {
            System.out.println("\n迁移分布:");
            migrationStats.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .forEach(entry ->
                            System.out.println("  " + entry.getKey() + ": " + entry.getValue())
                    );
        }
    }
}
