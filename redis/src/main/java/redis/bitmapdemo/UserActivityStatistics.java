package redis.bitmapdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.BitOP;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DAU/MAU 统计
 * 1.记录用户活跃
 *  a.日活
 *  b.月活
 *  c.设置7天过期（滚动统计）
 * 2.获取日活跃用户数
 * 3.获取月活跃用户数
 * 4.计算用户留存率（次日留存）-今天和昨天都活跃的用户
 * 5.获取用户活跃天数（在最近N天内）
 */
public class UserActivityStatistics {
    private Jedis jedis;
    private static final String DAILY_ACTIVE_PREFIX = "dau:";
    private static final String MONTHLY_ACTIVE_PREFIX = "mau:";
    private static final int BIT_SIZE = 8 * 8;
    public UserActivityStatistics(Jedis jedis) {
        this.jedis = jedis;
    }
    public void recordActive(String userId,LocalDate today) {
        if (today == null) {
            today = LocalDate.now();
        }
        //获取日的key
        String dailyKey = getDailyKey(today);
        long userIdHash = Math.abs(userId.hashCode()) % BIT_SIZE;
        jedis.setbit(dailyKey, userIdHash, true);

        // 设置7天过期（滚动统计）
        jedis.expire(dailyKey, 7 * 24 * 3600);

        // 记录月活跃
        String monthlyKey = getMonthlyKey(today);
        jedis.setbit(monthlyKey, userIdHash, true);
        jedis.expire(monthlyKey, 32 * 24 * 3600); // 32天，确保跨月
    }
    public Long getDAU(LocalDate date) {
        return jedis.bitcount(getDailyKey(date));
    }
    public Long getMAU(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return jedis.bitcount(getMonthlyKey(firstDay));
    }
    public double getNextDayRetention(LocalDate date) {
        String todayKey = getDailyKey(date);
        String yesterdayKey = getDailyKey(date.minusDays(1));
        long yesterdayActiveCount = jedis.bitcount(yesterdayKey);
        if (yesterdayActiveCount == 0.0){
            return 0.0;
        }
        // 创建留存Bitmap：今天和昨天都活跃的用户
        String retentionKey = "retention:" + date.format(DateTimeFormatter.ISO_DATE);
        jedis.bitop(BitOP.AND,retentionKey,todayKey,yesterdayKey);
        long retentionCount = jedis.bitcount(retentionKey);
        jedis.del(retentionKey);
        return (double) retentionCount /yesterdayActiveCount;

    }
    public int getUserActiveDays(String userId, int days) {
        long userIdHash = Math.abs(userId.hashCode()) % BIT_SIZE;
        LocalDate today = LocalDate.now();
        int activeDays = 0;
        for(int i = 0 ; i < days ; i++){
            LocalDate localDate = today.minusDays(i);
            String dailyKey = getDailyKey(localDate);
            if (jedis.getbit(dailyKey,userIdHash)){
                activeDays++;
            }
        }
        return activeDays;
    }
    private String getDailyKey(LocalDate date) {
        return DAILY_ACTIVE_PREFIX + date.format(DateTimeFormatter.ISO_DATE);
    }

    private String getMonthlyKey(LocalDate date) {
        return MONTHLY_ACTIVE_PREFIX + date.getYear() + "-" + date.getMonthValue();
    }
}
