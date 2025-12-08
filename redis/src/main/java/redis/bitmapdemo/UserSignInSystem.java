package redis.bitmapdemo;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 用户签到系统
 * 1.用户签到
 * 2.检查用户某天是否签到
 * 3.获取用户连续签到天数
 * 4.获取用户当月签到情况
 * 5.获取用户签到统计(签到次数/当月签到次数/连续签到次数)
 */
public class UserSignInSystem {
    private Jedis jedis;
    private static final String SIGN_PREFIX = "sign:";
    public UserSignInSystem(Jedis jedis){
        this.jedis = jedis;
    }
    //用户签到
    public void sign(String userId,LocalDate date){
        if (date == null){
            date = LocalDate.now();
        }
        //获取当前年的首天
        LocalDate firstOfYear = LocalDate.of(date.getYear(), 1, 1);
        //计算天数
        long days = ChronoUnit.DAYS.between(firstOfYear, date);
        //计算key
        String key = getSignKey(userId,date.getYear());
        //设置偏移量
        jedis.setbit(key,days,true);
        //设置过期时间
        long ttl = jedis.ttl(key);
        if (ttl < 0){
            LocalDate expireDate = LocalDate.of(date.getYear() + 1, 1, 1);
            long expireSeconds = ChronoUnit.SECONDS.between(
                    LocalDate.now().atStartOfDay(),
                    expireDate.atStartOfDay()
            );
            jedis.expire(key, expireSeconds);
        }
    }
    //检查用户某天是否签到
    public boolean isSigned(String userId , LocalDate date){
        if(date == null){
            date = LocalDate.now();
        }
        //获取当前年的首天
        LocalDate firstOfYear = LocalDate.of(date.getYear(), 1, 1);
        //计算天数
        long days = ChronoUnit.DAYS.between(firstOfYear, date);
        //计算key
        String key = getSignKey(userId,date.getYear());
        //获取偏移量
        return jedis.getbit(key,days);
    }
    /**
     * 获取用户连续签到天数
     */
    public long getContinuousSignDays(String userId){
        LocalDate date = LocalDate.now();
        //获取当前年的首天
        LocalDate firstOfYear = LocalDate.of(date.getYear(), 1, 1);
        //计算天数
        long days = ChronoUnit.DAYS.between(firstOfYear, date);
        //计算key
        String key = getSignKey(userId,date.getYear());
        //获取偏移量
        long continuousDays = 0;
        while (jedis.getbit(key,days)){
            continuousDays++;
            days--;
        }
        return continuousDays;
    }
    //获取用户本月签到情况
    public Set<String> getMonthSignStatus(String userId, int year, int month) {
        String key = getSignKey(userId, year);
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(
                firstDayOfMonth.lengthOfMonth()
        );

        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        long startOffset = ChronoUnit.DAYS.between(firstDayOfYear, firstDayOfMonth);
        long endOffset = ChronoUnit.DAYS.between(firstDayOfYear, lastDayOfMonth);

        Set<String> result = new LinkedHashSet<>();
        for (long offset = startOffset; offset <= endOffset; offset++) {
            boolean isSigned = jedis.getbit(key, offset);
            if (isSigned){
                LocalDate currentDate = firstDayOfYear.plusDays(offset);
                String dateStr = currentDate.format(DateTimeFormatter.ISO_DATE);
                result.add(dateStr);
            }
        }

        return result;
    }
    /**
     * 获取用户签到统计
     */
    public Map<String, Object> getSignStatistics(String userId, int year) {
        String key = getSignKey(userId, year);
        Map<String, Object> stats = new HashMap<>();

        // 总签到天数
        Long totalSignDays = jedis.bitcount(key);
        stats.put("「"+year+"」总签到天数", totalSignDays);

        // 当月签到天数
        LocalDate today = LocalDate.now();
        if (today.getYear() == year) {
            LocalDate firstDayOfMonth = LocalDate.of(year, today.getMonth(), 1);
            LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
            long startOffset = ChronoUnit.DAYS.between(firstDayOfYear, firstDayOfMonth);
            long endOffset = ChronoUnit.DAYS.between(firstDayOfYear, today);
            int monthSignDays = 0;
            for (long offset = startOffset; offset <= endOffset; offset++) {
                boolean isSigned = jedis.getbit(key, offset);
                if (isSigned){
                   monthSignDays++;
                }
            }
            stats.put("「"+year+"」"+today.getMonthValue()+"月签到天数", monthSignDays);
        }

        // 连续签到天数
        stats.put("「"+year+"」连续签到天数", getContinuousSignDays(userId));

        return stats;
    }
    private String getSignKey(String userId, int year){
        return SIGN_PREFIX + userId + ":" + year;
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.now().atStartOfDay());
        System.out.println(LocalDate.now().getYear());
    }
}
