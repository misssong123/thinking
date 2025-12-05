package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 基础排行榜类
 */
public class BasicLeaderboard {
    private final Jedis jedis;
    private final String namespace;

    public BasicLeaderboard(Jedis jedis, String namespace) {
        this.jedis = jedis;
        this.namespace = namespace;
    }

    private String getKey(String boardName, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s:%s:%s", namespace, boardName, dateStr);
    }

    /**
     * 添加或更新分数
     * @param operation ADD-累加, SET-设置, MAX-取最大值
     */
    public Long addScore(String boardName, String userId, double score, String operation) {
        String key = getKey(boardName, null);

        switch (operation.toUpperCase()) {
            case "ADD":
                return (long)jedis.zincrby(key, score, userId);
            case "SET":
                jedis.zadd(key, score, userId);
                return getRank(boardName, userId);
            case "MAX":
                Double current = jedis.zscore(key, userId);
                if (current == null || score > current) {
                    jedis.zadd(key, score, userId);
                }
                return getRank(boardName, userId);
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    /**
     * 获取用户排名（从1开始）
     */
    public Long getRank(String boardName, String userId) {
        String key = getKey(boardName, null);
        Long rank = jedis.zrevrank(key, userId);
        return rank == null ? null : rank + 1;
    }

    /**
     * 获取用户分数
     */
    public Double getScore(String boardName, String userId) {
        String key = getKey(boardName, null);
        return jedis.zscore(key, userId);
    }

    /**
     * 获取前N名
     */
    public List<Map.Entry<String, Double>> getTopN(String boardName, int n) {
        String key = getKey(boardName, null);
        List<Tuple> tuples = jedis.zrevrangeWithScores(key, 0, n - 1);

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            result.add(new AbstractMap.SimpleEntry<>(
                    tuple.getElement(),
                    tuple.getScore()
            ));
        }
        return result;
    }

    /**
     * 获取指定排名范围的用户
     */
    public List<Map.Entry<String, Double>> getRange(String boardName, long start, long end) {
        String key = getKey(boardName, null);
        List<Tuple> tuples = jedis.zrevrangeWithScores(key, start, end);

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            result.add(new AbstractMap.SimpleEntry<>(
                    tuple.getElement(),
                    tuple.getScore()
            ));
        }
        return result;
    }

    /**
     * 获取用户周边的排名（用于展示用户附近的竞争对手）
     */
    public List<Map.Entry<String, Double>> getAround(String boardName, String userId, int range) {
        String key = getKey(boardName, null);
        Long rank = jedis.zrevrank(key, userId);

        if (rank == null) {
            return Collections.emptyList();
        }

        long start = Math.max(0, rank - range);
        long end = rank + range;

        return getRange(boardName, start, end);
    }
}
