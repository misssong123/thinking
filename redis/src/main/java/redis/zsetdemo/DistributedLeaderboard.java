package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

/**
 * 分布式排行榜
 */
public class DistributedLeaderboard {
    private final Jedis jedis;
    private final int serverCount;

    public DistributedLeaderboard(Jedis jedis, int serverCount) {
        this.jedis = jedis;
        this.serverCount = serverCount;
    }

    /**
     * 在指定服务器上添加分数
     */
    public void addScoreToServer(String boardName, int serverId, String userId, double score) {
        String serverKey = getServerKey(boardName, serverId);
        String globalKey = getGlobalKey(boardName);

        try (Pipeline pipe = jedis.pipelined()) {
            // 更新服务器排行榜
            pipe.zadd(serverKey, score, userId);
            // 更新全局排行榜
            pipe.zadd(globalKey, score, userId);

            pipe.sync();
        }
    }

    /**
     * 合并所有服务器的排行榜
     */
    public void mergeAllServers(String boardName) {
        String globalKey = getGlobalKey(boardName);
        // 清空全局排行榜
        jedis.del(globalKey);
        // 合并所有服务器数据
        for (int serverId = 1; serverId <= serverCount; serverId++) {
            String serverKey = getServerKey(boardName, serverId);
            List<Tuple> users = jedis.zrangeWithScores(serverKey, 0, -1);
            if (!users.isEmpty()) {
                try (Pipeline pipe = jedis.pipelined()) {
                    Map<String, Double> scoreMap = new HashMap<>();
                    for (Tuple tuple : users) {
                        scoreMap.put(tuple.getElement(), tuple.getScore());
                    }
                    pipe.zadd(globalKey, scoreMap);
                    pipe.sync();
                }

            }
        }

        // 只保留前1000名
        jedis.zremrangeByRank(globalKey, 0, -1001);
    }

    /**
     * 获取全局排名
     */
    public Long getGlobalRank(String boardName, String userId) {
        String globalKey = getGlobalKey(boardName);
        Long rank = jedis.zrevrank(globalKey, userId);
        return rank == null ? null : rank + 1;
    }

    /**
     * 获取服务器内排名
     */
    public Long getServerRank(String boardName, int serverId, String userId) {
        String serverKey = getServerKey(boardName, serverId);
        Long rank = jedis.zrevrank(serverKey, userId);
        return rank == null ? null : rank + 1;
    }

    /**
     * 批量获取多个用户的全局排名
     */
    public Map<String, Long> batchGetGlobalRanks(String boardName, List<String> userIds) {
        String globalKey = getGlobalKey(boardName);
        Map<String, Long> result = new HashMap<>();

        try (Pipeline pipe = jedis.pipelined()) {
            Map<String, redis.clients.jedis.Response<Long>> responses = new HashMap<>();

            for (String userId : userIds) {
                responses.put(userId, pipe.zrevrank(globalKey, userId));
            }

            pipe.sync();

            for (Map.Entry<String, redis.clients.jedis.Response<Long>> entry : responses.entrySet()) {
                Long rank = entry.getValue().get();
                result.put(entry.getKey(), rank == null ? null : rank + 1);
            }
        }

        return result;
    }

    private String getGlobalKey(String boardName) {
        return String.format("rank:global:%s", boardName);
    }

    private String getServerKey(String boardName, int serverId) {
        return String.format("rank:server:%d:%s", serverId, boardName);
    }
}
