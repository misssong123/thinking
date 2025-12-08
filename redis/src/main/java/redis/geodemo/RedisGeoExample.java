package redis.geodemo;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.resps.GeoRadiusResponse;

import java.util.List;

/**
 * 精度与范围：Redis GEO的坐标有效范围为经度-180到180，纬度-85.05112878到85.05112878。
 *           其内部52位GeoHash编码提供了约1厘米的理论精度，完全满足常规应用需求。
 * 性能高效：由于基于内存和高效的数据结构，GEO查询速度极快。一个包含100万个点的集合进行半径查询，响应时间通常在几毫秒内。
 * 版本注意：请确保您的Redis服务器版本 ≥ 3.2。如需使用更先进的GEOSEARCH命令，则需升级至6.2或更高版本。
 */
public class RedisGeoExample {
    public static void main(String[] args) {
        try(Jedis jedis = RedisManager.getJedis()){
            String key = "landmarks";
            //添加地理位置
            jedis.geoadd(key, 116.404, 39.915, "Beijing");
            jedis.geoadd(key, 114.066, 22.543, "Shanghai");
            jedis.geoadd(key, 121.4737, 31.2304, "Shenzhen");
            // 2. 获取坐标
            List<GeoCoordinate> beijing = jedis.geopos(key, "Beijing");
            System.out.println(beijing);
            // 3. 计算距离
            Double geodist = jedis.geodist(key, "Beijing", "Shanghai", GeoUnit.M);
            System.out.println("Beijing to Shanghai distance: " + geodist + " meters");
            // 4. 查询附近地点（使用GEORADIUS，如需GEOSEARCH请使用更高版本Jedis）
            List<GeoRadiusResponse> georadius = jedis.georadius(key, 116.404, 39.915, 1000, GeoUnit.M);
            for (GeoRadiusResponse resp : georadius) {
                System.out.println(resp.getMemberByString());
            }
        }
    }
}
