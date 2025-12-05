package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存热点数据
 */
public class CachedLeaderboard {
    private final Jedis jedis;
    private final long cacheTimeoutSeconds;
    private final Map<String, CacheEntry> memoryCache;

    private static class CacheEntry {
        Object data;
        long timestamp;

        CacheEntry(Object data) {
            this.data = data;
            this.timestamp = Instant.now().getEpochSecond();
        }

        boolean isValid(long timeout) {
            return (Instant.now().getEpochSecond() - timestamp) < timeout;
        }
    }

    public CachedLeaderboard(Jedis jedis, long cacheTimeoutSeconds) {
        this.jedis = jedis;
        this.cacheTimeoutSeconds = cacheTimeoutSeconds;
        this.memoryCache = new ConcurrentHashMap<>();
    }

    /**
     * 获取前N名（带缓存）
     */
    @SuppressWarnings("unchecked")
    public List<Map.Entry<String, Double>> getTopWithCache(String boardName, int n) {
        String cacheKey = String.format("cache:rank:top:%s:%d", boardName, n);
        String timestampKey = String.format("%s:timestamp", cacheKey);

        // 检查Redis缓存
        String cachedData = jedis.get(cacheKey);
        String timestampStr = jedis.get(timestampKey);

        if (cachedData != null && timestampStr != null) {
            long timestamp = Long.parseLong(timestampStr);
            if (Instant.now().getEpochSecond() - timestamp < cacheTimeoutSeconds) {
                // Redis缓存有效
                return parseCachedData(cachedData);
            }
        }

        // 检查内存缓存
        CacheEntry memoryEntry = memoryCache.get(cacheKey);
        if (memoryEntry != null && memoryEntry.isValid(cacheTimeoutSeconds)) {
            return (List<Map.Entry<String, Double>>) memoryEntry.data;
        }

        // 缓存失效，重新获取
        String key = String.format("rank:%s", boardName);
        List<Tuple> tuples = jedis.zrevrangeWithScores(key, 0, n - 1);

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            result.add(new AbstractMap.SimpleEntry<>(
                    tuple.getElement(),
                    tuple.getScore()
            ));
        }

        // 更新Redis缓存
        if (!result.isEmpty()) {
            jedis.setex(cacheKey, cacheTimeoutSeconds, serializeData(result));
            jedis.setex(timestampKey, cacheTimeoutSeconds, String.valueOf(Instant.now().getEpochSecond()));
        }

        // 更新内存缓存
        memoryCache.put(cacheKey, new CacheEntry(result));

        return result;
    }

    /**
     * 获取用户排名（使用内存缓存）
     */
    private final Map<String, Long> userRankCache = new ConcurrentHashMap<>();

    public Long getUserRankCached(String boardName, String userId) {
        String cacheKey = String.format("user:rank:%s:%s", boardName, userId);

        // 检查缓存
        Long cachedRank = userRankCache.get(cacheKey);
        if (cachedRank != null) {
            return cachedRank;
        }

        // 计算排名
        String key = String.format("rank:%s", boardName);
        Long rank = jedis.zrevrank(key, userId);
        Long result = rank == null ? null : rank + 1;

        // 更新缓存
        userRankCache.put(cacheKey, result);

        return result;
    }

    public void clearUserCache(String boardName, String userId) {
        String cacheKey = String.format("user:rank:%s:%s", boardName, userId);
        userRankCache.remove(cacheKey);
    }

    public void clearAllCache() {
        userRankCache.clear();
        memoryCache.clear();
    }

    private String serializeData(List<Map.Entry<String, Double>> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : data) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    private List<Map.Entry<String, Double>> parseCachedData(String data) {
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        String[] items = data.split(";");

        for (String item : items) {
            if (!item.isEmpty()) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    String userId = parts[0];
                    Double score = Double.parseDouble(parts[1]);
                    result.add(new AbstractMap.SimpleEntry<>(userId, score));
                }
            }
        }

        return result;
    }
}
