package redis.zsetdemo;

import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LeaderboardDemo {
    public static void main(String[] args) {
        try (Jedis jedis = RedisManager.getJedis()) {
            // 1. 基础排行榜示例
            //basic(jedis);
            //2.实时积分榜（带时间衰减）
            //realTime(jedis);
            //3.分段排行榜示例
            //tiere(jedis);
            //4.分布式排行榜
            //distributed(jedis);
            //5.批量操作优化
            //batch(jedis);
            //6.缓存优化示例
            //cache(jedis);
            //7.完整游戏排行榜系统
            game(jedis);

        } finally {
            RedisManager.close();
        }
    }
    private static void batch(Jedis jedis) {
        OptimizedLeaderboard lb = new OptimizedLeaderboard(jedis,3);
        lb.addScoreBatch("score", "userId1", 100);
        lb.addScoreBatch("score", "userId2", 200);
        lb.addScoreBatch("score", "userId3", 300);
        lb.flushBatch("score");
        Map<String, Long> ranksBulk = lb.getRanksBulk("score", Arrays.asList("userId1", "userId2", "userId3"));
        System.out.println(ranksBulk);
    }
    private static void distributed(Jedis jedis) {
        DistributedLeaderboard distributedLb = new DistributedLeaderboard(jedis,3);
        distributedLb.addScoreToServer("score", 1,"userId1", 100);
        distributedLb.addScoreToServer("score", 2,"userId2", 200);
        distributedLb.addScoreToServer("score", 3,"userId3", 300);
        distributedLb.mergeAllServers("score");
        Map<String, Long> map = distributedLb.batchGetGlobalRanks("score", Arrays.asList("userId1", "userId2", "userId3"));
        System.out.println(map);
    }
    private static void realTime(Jedis jedis) {
        RealTimeLeaderboard realTimeLeaderboard = new RealTimeLeaderboard(jedis, 0.95,10);
        double user1Score = realTimeLeaderboard.addRealTimeScore("score", "userId1",100);
        System.out.println("user1Score: " + user1Score);
        double user2Score = realTimeLeaderboard.addRealTimeScore("score", "userId2",200);
        System.out.println("user2Score: " + user2Score);
        double user3Score = realTimeLeaderboard.addRealTimeScore("score", "userId3",300);
        System.out.println("user3Score: " + user3Score);
        //衰减
        realTimeLeaderboard.applyDecayToAll("score");
        //获取排行榜
        List<Map.Entry<String, Double>> top10 = realTimeLeaderboard.getTopN("score", 10);
        System.out.println("Top 10: " + top10);

    }
    private static void cache(Jedis jedis) {
        CachedLeaderboard cachedLb = new CachedLeaderboard(jedis, 300);
        List<Map.Entry<String, Double>> cachedTop = cachedLb.getTopWithCache("score", 10);
        System.out.println("Cached top: " + cachedTop);
        cachedTop = cachedLb.getTopWithCache("score", 10);
        System.out.println("Cached top: " + cachedTop);
    }

    private static void tiere(Jedis jedis) {
        TieredLeaderboard  tieredLb = new TieredLeaderboard(jedis);
        tieredLb.addScore("skill", "player1", 1200);
        tieredLb.addScore("skill", "player2", 2500);

        Map<String, Object> tierInfo = tieredLb.getUserTierInfo("skill", "player1");
        System.out.println("Player1 tier info: " + tierInfo);
        tierInfo = tieredLb.getUserTierInfo("skill", "player2");
        System.out.println("Player2 tier info: " + tierInfo);
    }

    private static void game(Jedis jedis) {
        GameLeaderboardSystem gameSystem = new GameLeaderboardSystem(jedis);

        // 模拟游戏结果
        gameSystem.recordGameResult("player1", 50.5, true);
        gameSystem.recordGameResult("player2", 30.0, false);
        gameSystem.recordGameResult("player3", 75.0, true);

        // 获取玩家统计
        Map<String, Object> stats = gameSystem.getUserStats("player1");
        System.out.println("Player1 stats: " + stats);

        // 获取每日排行榜
        List<Map.Entry<String, Double>> dailyTop = gameSystem.getDailyTop(10);
        System.out.println("Daily top: " + dailyTop);
    }

    private static void basic(Jedis jedis) {
        BasicLeaderboard basicLb = new BasicLeaderboard(jedis, "demo");
        basicLb.addScore("score", "user1", 100, "ADD");
        basicLb.addScore("score", "user2", 150, "ADD");

        Long rank = basicLb.getRank("score", "user1");
        System.out.println("User1 rank: " + rank);

        List<Map.Entry<String, Double>> top10 = basicLb.getTopN("score", 10);
        System.out.println("Top 10: " + top10);
    }
}
