package com.meng.specialSubject.knapsack.diffRoute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个 互不相同 的整数数组，其中 locations[i] 表示第 i 个城市的位置。同时给你 start，finish
 * 和 fuel 分别表示出发城市、目的地城市和你初始拥有的汽油总量
 * 每一步中，如果你在城市 i ，你可以选择任意一个城市 j ，满足  j != i 且 0 <= j < locations.length ，
 * 并移动到城市 j 。从城市 i 移动到 j 消耗的汽油量为 |locations[i] - locations[j]|，|x| 表示 x 的绝对值。
 * 请注意， fuel 任何时刻都 不能 为负，且你 可以 经过任意城市超过一次（包括 start 和 finish ）。
 * 请你返回从 start 到 finish 所有可能路径的数目。
 * 由于答案可能很大， 请将它对 10^9 + 7 取余后返回。
 */
public class Q007CountRoutes {
    Map<String,Integer> numCache = new HashMap<>();
    int N = 1000000007;

    /**
     * 执行用时分布
     * 900
     * ms
     * 击败
     * 5.06%
     * 复杂度分析
     * 消耗内存分布
     * 50.09
     * MB
     * 击败
     * 5.70%
     * @param locations
     * @param start
     * @param finish
     * @param fuel
     * @return
     */
    public int countRoutesMy(int[] locations, int start, int finish, int fuel) {
        int num = start == finish? 1 : 0;
        return dfs(locations,start,finish,fuel)+num;
    }

    private int dfsMy(int[] locations, int start, int finish, int fuel) {
        if (numCache.containsKey(start+":"+fuel)){
            return numCache.get(start+":"+fuel);
        }
        int temp = 0;
        for(int i= 0;i < locations.length;i++){
            if(i!=start&&fuel-Math.abs(locations[start]-locations[i])>=0){
                if (i==finish){
                    temp++;
                }
                temp = (temp+dfsMy(locations,i,finish,fuel-Math.abs(locations[start]-locations[i])))%N;
            }
        }
        numCache.put(start+":"+fuel,temp);
        return temp;
    }

    int mod = 1000000007;
    // 缓存器：用于记录「特定状态」下的结果
    // cache[i][fuel] 代表从位置 i 出发，当前剩余的油量为 fuel 的前提下，到达目标位置的「路径数量」
    int[][] cache;
    /**
     * 执行用时分布
     * 37
     * ms
     * 击败
     * 77.22%
     * 复杂度分析
     * 消耗内存分布
     * 43.23
     * MB
     * 击败
     * 22.15%
     * @param ls
     * @param start
     * @param end
     * @param fuel
     * @return
     */
    public int countRoutes(int[] ls, int start, int end, int fuel) {
        int n = ls.length;

        // 初始化缓存器
        // 之所以要初始化为 -1
        // 是为了区分「某个状态下路径数量为 0」和「某个状态尚未没计算过」两种情况
        cache = new int[n][fuel + 1];
        for (int i = 0; i < n; i++) {
            Arrays.fill(cache[i], -1);
        }

        return dfs(ls, start, end, fuel);
    }

    /**
     * 计算「路径数量」
     * @param ls 入参 locations
     * @param u 当前所在位置（ls 的下标）
     * @param end 目标哦位置（ls 的下标）
     * @param fuel 剩余油量
     * @return 在位置 u 出发，油量为 fuel 的前提下，到达 end 的「路径数量」
     */
    int dfs(int[] ls, int u, int end, int fuel) {
        // 如果缓存中已经有答案，直接返回
        if (cache[u][fuel] != -1) {
            return cache[u][fuel];
        }

        // 如果一步到达不了，说明从位置 u 不能到达 end 位置
        // 将结果 0 写入缓存器并返回
        int need = Math.abs(ls[u] - ls[end]);
        if (need > fuel) {
            cache[u][fuel] = 0;
            return 0;
        }

        int n = ls.length;
        // 计算油量为 fuel，从位置 u 到 end 的路径数量
        // 由于每个点都可以经过多次，如果 u = end，那么本身就算一条路径
        int sum = u == end ? 1 : 0;
        for (int i = 0; i < n; i++) {
            if (i != u) {
                need = Math.abs(ls[i] - ls[u]);
                if (fuel >= need) {
                    sum += dfs(ls, i, end, fuel - need);
                    sum %= mod;
                }
            }
        }
        cache[u][fuel] = sum;
        return sum;
    }
}
