package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

/**
 * 多维度排行榜
 */
public class MultiDimensionLeaderboard {
    private final Jedis jedis;
    private final Map<String, Double> dimensionWeights;

    public MultiDimensionLeaderboard(Jedis jedis) {
        this.jedis = jedis;
        this.dimensionWeights = new HashMap<>();
        initDefaultWeights();
    }

    private void initDefaultWeights() {
        dimensionWeights.put("gold", 0.3);
        dimensionWeights.put("exp", 0.3);
        dimensionWeights.put("win_rate", 0.4);
    }

    public void setWeight(String dimension, double weight) {
        dimensionWeights.put(dimension, weight);
    }

    /**
     * 添加多个维度分数
     */
    public void addScoreMulti(String userId, Map<String, Double> scores) {
        try (Pipeline pipe = jedis.pipelined()) {
            // 更新各个维度
            for (Map.Entry<String, Double> entry : scores.entrySet()) {
                String dimension = entry.getKey();
                Double score = entry.getValue();
                String key = String.format("rank:dimension:%s", dimension);
                pipe.zadd(key, score, userId);
            }

            // 计算综合分数
            double totalScore = calculateTotalScore(scores);
            pipe.zadd("rank:total", totalScore, userId);

            pipe.sync();
        }
    }

    private double calculateTotalScore(Map<String, Double> scores) {
        double total = 0;
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            String dimension = entry.getKey();
            Double score = entry.getValue();
            Double weight = dimensionWeights.get(dimension);
            if (weight != null) {
                total += score * weight;
            }
        }
        return total;
    }

    /**
     * 获取用户在各个维度的排名信息
     */
    public Map<String, Map<String, Object>> getUserRankings(String userId) {
        Map<String, Map<String, Object>> result = new HashMap<>();

        // 添加所有维度的key
        Set<String> dimensions = new HashSet<>(dimensionWeights.keySet());
        dimensions.add("total");

        try (Pipeline pipe = jedis.pipelined()) {
            Map<String, redis.clients.jedis.Response<Long>> rankResponses = new HashMap<>();
            Map<String, redis.clients.jedis.Response<Double>> scoreResponses = new HashMap<>();

            for (String dimension : dimensions) {
                String key = String.format("rank:dimension:%s", dimension);
                rankResponses.put(dimension, pipe.zrevrank(key, userId));
                scoreResponses.put(dimension, pipe.zscore(key, userId));
            }

            pipe.sync();

            // 组装结果
            for (String dimension : dimensions) {
                Long rank = rankResponses.get(dimension).get();
                Double score = scoreResponses.get(dimension).get();

                Map<String, Object> info = new HashMap<>();
                info.put("rank", rank == null ? null : rank + 1);
                info.put("score", score);

                result.put(dimension, info);
            }
        }

        return result;
    }
}
