package com.meng.specialSubject.knapsack.diffRoute;
/**Q007CountRoutes动态规划写法
 * 给你一个 互不相同 的整数数组，其中 locations[i] 表示第 i 个城市的位置。同时给你 start，finish
 * 和 fuel 分别表示出发城市、目的地城市和你初始拥有的汽油总量
 * 每一步中，如果你在城市 i ，你可以选择任意一个城市 j ，满足  j != i 且 0 <= j < locations.length ，
 * 并移动到城市 j 。从城市 i 移动到 j 消耗的汽油量为 |locations[i] - locations[j]|，|x| 表示 x 的绝对值。
 * 请注意， fuel 任何时刻都 不能 为负，且你 可以 经过任意城市超过一次（包括 start 和 finish ）。
 * 请你返回从 start 到 finish 所有可能路径的数目。
 * 由于答案可能很大， 请将它对 10^9 + 7 取余后返回。
 *
 */
public class Q008CountRoutes {
    /**
     * 1.寻找变量
     * 2.推导转移公式
     * 执行用时分布
     * 158
     * ms
     * 击败
     * 25.32%
     * 复杂度分析
     * 消耗内存分布
     * 43.16
     * MB
     * 击败
     * 33.55%
     * @param locations
     * @param start
     * @param finish
     * @param fuel
     * @return
     */
    public int countRoutesMy(int[] locations, int start, int finish, int fuel) {
        int n = locations.length;
        int[][] cache = new int[n][fuel+1];
        //所有终点加1
        for (int i = 0; i <= fuel; i++){
            cache[finish][i]=1;
        }
        int N = 1000000007;
        //计算不同油量时，不同节点的可能性
        for(int i = 0 ; i <= fuel ; i++){
            for (int j = 0 ; j < n ; j++){
                for(int k = 0 ; k < n ; k++){
                    if (j != k&& Math.abs(locations[j] - locations[k]) <= i){
                        cache[j][i] += cache[k][i - Math.abs(locations[j] - locations[k])];
                        cache[j][i] %= N;
                    }
                }
            }
        }
        return cache[start][fuel];
    }
}
