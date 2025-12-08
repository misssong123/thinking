package redis.bitmapdemo;

import redis.clients.jedis.Jedis;

/**
 * bitmap 基础操作
 */
public class BitmapBasics {
    private Jedis jedis;
    private String key ;
    public BitmapBasics(Jedis jedis,String key) {
        this.jedis = jedis;
        this.key = key;
    }
    //设置偏移量
    public void setOffset( long offset, boolean value) {
        jedis.setbit(key, offset, value);
    }
    //获取偏移量
    public boolean getOffset(long offset) {
        return jedis.getbit(key, offset);
    }
    //统计偏移量为1的数量
    public long count() {
        return jedis.bitcount(key);
    }
    //统计子节偏移量在[start,end]之间为1的数量
    public long count(long start, long end) {
        return jedis.bitcount(key, start, end);
    }
    //bitPos()方法用于计算位图中第一个值为1或0的偏移量
    public long bitPos(boolean value) {
        return jedis.bitpos(key, value);
    }

    public static void main(String[] args) {
        try(Jedis jedis = RedisManager.getJedis()){
            BitmapBasics demo = new BitmapBasics(jedis, "bitmap");
            demo.setOffset(0, true);
            demo.setOffset(1, false);
            demo.setOffset(2, true);
            demo.setOffset(9, true);
            System.out.println("0:"+demo.getOffset(0));
            System.out.println("1:"+demo.getOffset(1));
            System.out.println("2:"+demo.getOffset(2));
            System.out.println("demo.count():"+demo.count());
            System.out.println("demo.count(0, 2):"+demo.count(0, 1));
            System.out.println("demo.bitPos(true):"+demo.bitPos(true));
            System.out.println("demo.bitPos(false):"+demo.bitPos(false));
        }
    }
}
