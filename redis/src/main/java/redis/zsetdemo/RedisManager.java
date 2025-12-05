package redis.zsetdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
    private static JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMinIdle(10);
        config.setMaxWaitMillis(3000);
        jedisPool = new JedisPool(config, "test-teg-yxpt01.rdb.58dns.org", 50029);
    }

    public static Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.auth("3c78d83a7c0d8da0");
        return jedis;
    }

    public static void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}