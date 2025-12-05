package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.resps.Tuple;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实时积分榜（带时间衰减）
 */
public class RealTimeLeaderboard {
    private final Jedis jedis;
    private final double decayRate;  // 衰减系数
    private final long decayIntervalSeconds;  // 衰减间隔（秒）

    public RealTimeLeaderboard(Jedis jedis, double decayRate, long decayIntervalSeconds) {
        this.jedis = jedis;
        this.decayRate = decayRate;
        this.decayIntervalSeconds = decayIntervalSeconds;
    }

    public RealTimeLeaderboard(Jedis jedis) {
        this(jedis, 0.95, 600); // 默认每10分钟衰减一次
    }

    /**
     * 添加实时分数
     */
    public double addRealTimeScore(String boardName, String userId, double points) {
        String key = getKey(boardName);
        String timestampKey = getTimestampKey(boardName, userId);

        long now = Instant.now().getEpochSecond();

        // 获取当前分数和最后更新时间
        Double currentScore = jedis.zscore(key, userId);
        String lastUpdateStr = jedis.get(timestampKey);

        long lastUpdate = lastUpdateStr != null ? Long.parseLong(lastUpdateStr) : now;

        // 计算衰减
        if (currentScore != null) {
            long timePassed = now - lastUpdate;
            if (timePassed > 0) {
                long decayCycles = timePassed / decayIntervalSeconds;
                double decayFactor = Math.pow(decayRate, decayCycles);
                currentScore = currentScore * decayFactor;
            }
        } else {
            currentScore = 0.0;
        }

        // 更新分数
        double newScore = currentScore + points;
        jedis.zadd(key, newScore, userId);
        jedis.set(timestampKey, String.valueOf(now));

        return newScore;
    }

    /**
     * 对所有用户应用衰减
     */
    public void applyDecayToAll(String boardName) {
        String key = getKey(boardName);
        long now = Instant.now().getEpochSecond();

        List<String> users = jedis.zrange(key, 0, -1);
        if (users.isEmpty()) {
            return;
        }

        try (Pipeline pipe = jedis.pipelined()) {
            for (String userId : users) {
                String timestampKey = getTimestampKey(boardName, userId);
                String lastUpdateStr = jedis.get(timestampKey);

                if (lastUpdateStr != null) {
                    long lastUpdate = Long.parseLong(lastUpdateStr);
                    long timePassed = now - lastUpdate;

                    if (timePassed > 0) {
                        Double currentScore = jedis.zscore(key, userId);
                        if (currentScore != null) {
                            long decayCycles = timePassed / decayIntervalSeconds;
                            double decayFactor = Math.pow(decayRate, decayCycles);
                            double newScore = currentScore * decayFactor;

                            pipe.zadd(key, newScore, userId);
                            pipe.set(timestampKey, String.valueOf(now));
                        }
                    }
                }
            }
            pipe.sync();
        }
    }

    private String getKey(String boardName) {
        return String.format("rank:realtime:%s", boardName);
    }

    private String getTimestampKey(String boardName, String userId) {
        return String.format("rank:realtime:%s:timestamp:%s", boardName, userId);
    }
    public List<Map.Entry<String, Double>> getTopN(String boardName, int n) {
        List<Tuple> tuples = jedis.zrevrangeWithScores(getKey(boardName), 0, n - 1);
        return tuples.stream().map(tuple -> new AbstractMap.SimpleEntry<>(tuple.getElement(), tuple.getScore())).collect(Collectors.toList());
    }
}