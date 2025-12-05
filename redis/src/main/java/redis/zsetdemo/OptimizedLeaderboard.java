package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import java.util.*;

/**
 * 批量操作优化
 */
public class OptimizedLeaderboard {
    private final Jedis jedis;
    private final int batchSize;
    private final Map<String, List<ScoreEntry>> batchBuffer;

    private static class ScoreEntry {
        String userId;
        double score;

        ScoreEntry(String userId, double score) {
            this.userId = userId;
            this.score = score;
        }
    }

    public OptimizedLeaderboard(Jedis jedis, int batchSize) {
        this.jedis = jedis;
        this.batchSize = batchSize;
        this.batchBuffer = new HashMap<>();
    }

    /**
     * 批量添加分数（内存缓冲）
     */
    public void addScoreBatch(String boardName, String userId, double score) {
        batchBuffer.computeIfAbsent(boardName, k -> new ArrayList<>())
                .add(new ScoreEntry(userId, score));

        // 达到批处理大小时执行
        if (batchBuffer.get(boardName).size() >= batchSize) {
            flushBatch(boardName);
        }
    }

    /**
     * 刷新缓冲到Redis
     */
    public void flushBatch(String boardName) {
        List<ScoreEntry> entries = batchBuffer.get(boardName);
        if (entries == null || entries.isEmpty()) {
            return;
        }

        String key = String.format("rank:%s", boardName);
        Map<String, Double> scoreMap = new HashMap<>();

        for (ScoreEntry entry : entries) {
            scoreMap.put(entry.userId, entry.score);
        }

        jedis.zadd(key, scoreMap);
        batchBuffer.put(boardName, new ArrayList<>());
    }

    public void flushAll() {
        for (String boardName : batchBuffer.keySet()) {
            flushBatch(boardName);
        }
    }

    /**
     * 批量获取多个用户的排名
     */
    public Map<String, Long> getRanksBulk(String boardName, List<String> userIds) {
        String key = String.format("rank:%s", boardName);
        Map<String, Long> result = new HashMap<>();

        try (Pipeline pipe = jedis.pipelined()) {
            Map<String, redis.clients.jedis.Response<Long>> responses = new HashMap<>();

            for (String userId : userIds) {
                responses.put(userId, pipe.zrevrank(key, userId));
            }

            pipe.sync();

            for (Map.Entry<String, redis.clients.jedis.Response<Long>> entry : responses.entrySet()) {
                Long rank = entry.getValue().get();
                result.put(entry.getKey(), rank == null ? null : rank + 1);
            }
        }

        return result;
    }
}
