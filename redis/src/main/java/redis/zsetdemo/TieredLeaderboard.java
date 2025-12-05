package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

/**
 * 分段排行榜
 */
public class TieredLeaderboard {
    private final Jedis jedis;
    private final Map<String, TierConfig> tierConfigs;

    public static class TierConfig {
        private final String name;
        private final double minScore;
        private final double maxScore;

        public TierConfig(String name, double minScore, double maxScore) {
            this.name = name;
            this.minScore = minScore;
            this.maxScore = maxScore;
        }

        public boolean inTier(double score) {
            return score >= minScore && score < maxScore;
        }
    }

    public TieredLeaderboard(Jedis jedis) {
        this.jedis = jedis;
        this.tierConfigs = new LinkedHashMap<>();
        initDefaultTiers();
    }

    private void initDefaultTiers() {
        tierConfigs.put("bronze", new TierConfig("bronze", 0, 1000));
        tierConfigs.put("silver", new TierConfig("silver", 1000, 2000));
        tierConfigs.put("gold", new TierConfig("gold", 2000, 3000));
        tierConfigs.put("platinum", new TierConfig("platinum", 3000, 4000));
        tierConfigs.put("diamond", new TierConfig("diamond", 4000, Double.MAX_VALUE));
    }

    public void addTier(String name, double minScore, double maxScore) {
        tierConfigs.put(name, new TierConfig(name, minScore, maxScore));
    }

    /**
     * 添加分数并自动分段
     */
    public void addScore(String boardName, String userId, double score) {
        String mainKey = getMainKey(boardName);
        jedis.zadd(mainKey, score, userId);

        // 找到用户应该所在的分段
        TierConfig targetTier = null;
        for (TierConfig tier : tierConfigs.values()) {
            if (tier.inTier(score)) {
                targetTier = tier;
                break;
            }
        }

        if (targetTier != null) {
            // 添加到目标分段
            String tierKey = getTierKey(boardName, targetTier.name);
            jedis.zadd(tierKey, score, userId);

            // 从其他分段移除
            removeFromOtherTiers(boardName, userId, targetTier.name);
        }
    }

    private void removeFromOtherTiers(String boardName, String userId, String currentTierName) {
        for (String tierName : tierConfigs.keySet()) {
            if (!tierName.equals(currentTierName)) {
                String tierKey = getTierKey(boardName, tierName);
                jedis.zrem(tierKey, userId);
            }
        }
    }

    /**
     * 获取指定分段的前N名
     */
    public List<Map.Entry<String, Double>> getTierTop(String boardName, String tierName, int n) {
        String tierKey = getTierKey(boardName, tierName);
        List<Tuple> tuples = jedis.zrevrangeWithScores(tierKey, 0, n - 1);

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
     * 获取用户所在分段信息
     */
    public Map<String, Object> getUserTierInfo(String boardName, String userId) {
        String mainKey = getMainKey(boardName);
        Double score = jedis.zscore(mainKey, userId);

        if (score == null) {
            return null;
        }

        for (TierConfig tier : tierConfigs.values()) {
            if (tier.inTier(score)) {
                String tierKey = getTierKey(boardName, tier.name);
                Long rankInTier = jedis.zrevrank(tierKey, userId);

                Map<String, Object> result = new HashMap<>();
                result.put("tier", tier.name);
                result.put("rank_in_tier", rankInTier == null ? null : rankInTier + 1);
                result.put("score", score);
                result.put("min_score", tier.minScore);
                result.put("max_score", tier.maxScore);

                return result;
            }
        }

        return null;
    }

    private String getMainKey(String boardName) {
        return String.format("rank:tier:main:%s", boardName);
    }

    private String getTierKey(String boardName, String tierName) {
        return String.format("rank:tier:%s:%s", boardName, tierName);
    }
}
