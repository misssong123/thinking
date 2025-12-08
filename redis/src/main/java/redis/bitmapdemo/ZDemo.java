package redis.bitmapdemo;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * 测试用例
 */
public class ZDemo {
    public static void main(String[] args) {
        try(Jedis jedis = RedisManager.getJedis()){
            //用户签到系统
            //userSignInSystemTest(jedis);
            //天/月统计
            //dayMonthStatistics(jedis);
            //布隆过滤器
            bloomFilterTest(jedis);
        }

    }

    private static void bloomFilterTest(Jedis jedis) {
        // 创建布隆过滤器：预期插入100万元素，误判率0.1%
        BloomFilter bloomFilter = new BloomFilter(jedis, "bloom:users",
                100, 0.1);
        // 添加元素
        bloomFilter.add("user123");
        bloomFilter.add("user456");

        // 检查元素是否存在
        System.out.println("Contains user123: " + bloomFilter.mightContain("user123"));
        System.out.println("Contains user999: " + bloomFilter.mightContain("user999"));

        // 获取统计信息
        System.out.println("Bits set: " + bloomFilter.getBitsSet());
        System.out.println("Theoretical false positive rate: " +
                bloomFilter.getFalsePositiveRate(2));
    }

    private static void dayMonthStatistics(Jedis jedis) {
        UserActivityStatistics demo = new UserActivityStatistics(jedis);
        List<String> users = Arrays.asList("wang","li","zhao");
        LocalDate now = LocalDate.now();
        //记录日活数据
        for(int i = 0 ; i < 3 ; i++){
            for (String user : users){
                LocalDate date = now.minusDays(i);
                demo.recordActive(user,date);
            }
        }
        //统计日活
        System.out.println("日活："+demo.getDAU(now));
        //统计月活
        System.out.println("月活："+demo.getMAU(now.getYear(),now.getMonthValue()));
        //统计留存率
        System.out.println("留存率："+demo.getNextDayRetention(now));
        //统计用户活跃天数（在最近N天内）
        System.out.println("用户活跃天数（在最近3天内）："+demo.getUserActiveDays("wang",3));
    }

    private static void userSignInSystemTest(Jedis jedis) {
        List<String> users = Arrays.asList("wang","li","zhao");
        UserSignInSystem demo = new UserSignInSystem(jedis);
        //用户签到
        LocalDate now = LocalDate.now();
        /*//前十天-前五天连续签到，前两天至今连续签到
        for(int i = 10 ; i >=0 ; i--){
            if (i<=4&&i>=2){
                continue;
            }
            LocalDate localDate = now.minusDays(i);
            for (String user : users){
                demo.sign(user,localDate);
            }
        }*/
        //校验用户今天是否签到
        /*for (String user : users){
            System.out.println(user+"今天是否签到："+demo.isSigned(user,now));
        }*/
        //获取用户连续签到天数
        /*for (String user : users){
            System.out.println(user+"连续签到天数："+demo.getContinuousSignDays(user));
        }*/
        //获取本月签到情况
        /*for(String user : users){
            System.out.println(user+"本月签到情况："+demo.getMonthSignStatus(user,now.getYear(),now.getMonthValue()));
        }*/
        //获取用户签到情况
        for(String user : users){
            System.out.println(user+"签到统计："+demo.getSignStatistics(user,now.getYear()));
        }

    }
}
