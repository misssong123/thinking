package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 完整游戏排行榜系统
 */
public class GameLeaderboardSystem {
    private final Jedis jedis;
    private final BasicLeaderboard dailyLeaderboard;
    private final BasicLeaderboard seasonLeaderboard;
    private final RealTimeLeaderboard realTimeLeaderboard;

    public GameLeaderboardSystem(Jedis jedis) {
        this.jedis = jedis;
        this.dailyLeaderboard = new BasicLeaderboard(jedis, "game");
        this.seasonLeaderboard = new BasicLeaderboard(jedis, "game:season");
        this.realTimeLeaderboard = new RealTimeLeaderboard(jedis);
    }

    /**
     * 记录游戏结果
     */
    public void recordGameResult(String userId, double scoreChange, boolean win) {
        // 1. 更新每日榜
        dailyLeaderboard.addScore("daily_score", userId, scoreChange, "ADD");

        // 2. 更新赛季榜
        seasonLeaderboard.addScore("season_score", userId, scoreChange, "ADD");

        // 3. 更新实时活跃榜
        double points = win ? 10.0 : 1.0;
        realTimeLeaderboard.addRealTimeScore("activity", userId, points);

        // 4. 更新胜场统计
        if (win) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String winKey = String.format("game:stats:wins:%s", today);
            jedis.hincrBy(winKey, userId, 1);
        }

        // 5. 更新用户统计数据
        String statsKey = String.format("game:stats:%s", userId);
        jedis.hincrBy(statsKey, "games_played", 1);
        if (win) {
            jedis.hincrBy(statsKey, "games_won", 1);
        }
        jedis.hincrByFloat(statsKey, "total_score", scoreChange);
    }

    /**
     * 获取用户统计数据和排名
     */
    public Map<String, Object> getUserStats(String userId) {
        String statsKey = String.format("game:stats:%s", userId);
        Map<String, String> stats = jedis.hgetAll(statsKey);

        // 获取排名
        Long dailyRank = dailyLeaderboard.getRank("daily_score", userId);
        Long seasonRank = seasonLeaderboard.getRank("season_score", userId);

        // 获取分数
        Double dailyScore = dailyLeaderboard.getScore("daily_score", userId);
        Double seasonScore = seasonLeaderboard.getScore("season_score", userId);

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);

        Map<String, Object> rankings = new HashMap<>();
        rankings.put("daily", dailyRank);
        rankings.put("season", seasonRank);
        result.put("rankings", rankings);

        result.put("daily_score", dailyScore);
        result.put("season_score", seasonScore);

        // 计算胜率
        if (stats != null) {
            try {
                int gamesPlayed = Integer.parseInt(stats.getOrDefault("games_played", "0"));
                int gamesWon = Integer.parseInt(stats.getOrDefault("games_won", "0"));
                double winRate = gamesPlayed > 0 ? (double) gamesWon / gamesPlayed * 100 : 0;
                result.put("win_rate", String.format("%.2f%%", winRate));
            } catch (NumberFormatException e) {
                result.put("win_rate", "0%");
            }
        }

        return result;
    }

    /**
     * 获取每日Top N
     */
    public List<Map.Entry<String, Double>> getDailyTop(int n) {
        return dailyLeaderboard.getTopN("daily_score", n);
    }

    /**
     * 重置每日排行榜
     */
    public void resetDailyRank() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate oldDate = today.minusDays(31);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 归档昨日数据
        String yesterdayKey = String.format("game:daily_score:%s", yesterday.format(formatter));
        String archiveKey = String.format("archive:game:daily_score:%s", yesterday.format(formatter));

        if (jedis.exists(yesterdayKey)) {
            jedis.rename(yesterdayKey, archiveKey);
        }

        // 清理30天前的归档数据
        String oldKey = String.format("archive:game:daily_score:%s", oldDate.format(formatter));
        jedis.del(oldKey);
    }

    /**
     * 获取用户在所有排行榜中的综合信息
     */
    public Map<String, Object> getUserComprehensiveInfo(String userId) {
        Map<String, Object> result = new HashMap<>();

        // 基本信息
        result.put("user_id", userId);

        // 每日榜信息
        result.put("daily_rank", dailyLeaderboard.getRank("daily_score", userId));
        result.put("daily_score", dailyLeaderboard.getScore("daily_score", userId));

        // 赛季榜信息
        result.put("season_rank", seasonLeaderboard.getRank("season_score", userId));
        result.put("season_score", seasonLeaderboard.getScore("season_score", userId));

        // 实时榜信息
        String realtimeKey = "rank:realtime:activity";
        String timestampKey = String.format("rank:realtime:activity:timestamp:%s", userId);
        result.put("activity_score", jedis.zscore(realtimeKey, userId));
        result.put("last_active", jedis.get(timestampKey));

        // 统计信息
        result.putAll(getUserStats(userId));

        return result;
    }
}
