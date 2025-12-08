package redis.bitmapdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.BitOP;

/**
 * 1.用户上线，位图两分钟过期
 * 2.获取当前在线用户数（最近1分钟内活跃）
 * 3.检查用户是否在线
 * 4.获取用户最后活跃时间
 */
public class OnlineStatusSystem {
    private Jedis jedis;
    private static final String ONLINE_PREFIX = "online:";
    private static final int SLOT_COUNT = 60; // 60秒，每分钟一个位图
    private static final int BIT_SIZE = 8 * 8;
    public OnlineStatusSystem(Jedis jedis) {
        this.jedis = jedis;
    }
    public void userOnline(String userId) {
        long userIdHash = Math.abs(userId.hashCode()) % BIT_SIZE;
        long cur = System.currentTimeMillis() / 1000;
        int solt = (int)cur % SLOT_COUNT;
        //存储用户活跃
        jedis.setbit(getOnlineKey(solt), userIdHash, true);
        //设置过期时间两分钟
        jedis.expire(getOnlineKey(solt), 120);
    }

    /**
     * 获取当前在线用户数（最近1分钟内活跃）
     * @return
     */
    public Long getOnlineUserCount() {
        long timestamp = System.currentTimeMillis() / 1000;
        // 合并最近60秒的数据
        String[] slotKeys = new String[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; i++) {
            slotKeys[i] = getOnlineKey(i);
        }

        String resultKey = "online:current";
        jedis.bitop(BitOP.OR, resultKey, slotKeys);
        Long count = jedis.bitcount(resultKey);
        jedis.del(resultKey);
        return count;
    }
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        long userIdHash = Math.abs(userId.hashCode()) % BIT_SIZE;

        // 检查最近30秒内的所有slot
        long timestamp = System.currentTimeMillis() / 1000;
        for (int i = 0; i < 30; i++) {
            int slot = (int) ((timestamp - i) % SLOT_COUNT);
            String key = getOnlineKey(slot);
            if (jedis.getbit(key, userIdHash)) {
                return true;
            }
        }

        return false;
    }
    /**
     * 获取用户最后活跃时间
     */
    public Long getUserLastActiveTime(String userId) {
        long userIdHash = Math.abs(userId.hashCode()) % BIT_SIZE;
        long currentTime = System.currentTimeMillis() / 1000;
        // 向前查找最近60秒
        for (int i = 0; i < SLOT_COUNT; i++) {
            long checkTime = currentTime - i;
            int slot = (int) (checkTime % SLOT_COUNT);
            String key = getOnlineKey(slot);
            if (jedis.getbit(key, userIdHash)) {
                return checkTime;
            }
        }
        return null;
    }
    private String getOnlineKey(int slot) {
        return ONLINE_PREFIX + slot;
    }
}
