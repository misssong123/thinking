package com.meng.specialSubject.knapsack.knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Q004NumSquares {
    /**
     * 执行用时分布
     * 26
     * ms
     * 击败
     * 71.36%
     * 复杂度分析
     * 消耗内存分布
     * 41.88
     * MB
     * 击败
     * 12.68%
     * @param n
     * @return
     */
    public int numSquaresMy(int n) {
        List<Integer> cache = new ArrayList<>();
        for(int i = 1 ; i * i <= n ; i++){
            cache.add(i*i);
        }
        if (cache.contains(n)){
            return 1;
        }
        int[] dp = new int[n+1];
        Arrays.fill(dp,n);
        for(int num : cache){
            dp[num] = 1;
            for(int i = num+1 ; i <= n ; i++){
                dp[i] = Math.min(dp[i],dp[i-num]+1);
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        Q004NumSquares q004NumSquares = new Q004NumSquares();
        System.out.println(q004NumSquares.numSquares(12));
    }
    int INF = -1;

    /**
     * 执行用时分布
     * 29
     * ms
     * 击败
     * 53.03%
     * 复杂度分析
     * 消耗内存分布
     * 41.98
     * MB
     * 击败
     * 11.13%
     * @param n
     * @return
     */
    public int numSquares(int n) {
        // 预处理出所有可能用到的「完全平方数」
        List<Integer> list = new ArrayList<>();
        int idx = 1;
        while (idx * idx <= n) {
            list.add(idx * idx);
            idx++;
        }

        // f[j] 代表考虑到当前物品为止，凑出 j 所使用到的最小元素个数
        int len = list.size();
        int[] f = new int[n + 1];

        // 处理第一个数的情况
        for (int j = 0; j <= n; j++) {
            int t = list.get(0);
            int k = j / t;
            if (k * t == j) { // 只有容量为第一个数的整数倍的才能凑出
                f[j] = k;
            } else { // 其余则为无效值
                f[j] = INF;
            }
        }

        // 处理剩余数的情况
        for (int i = 1; i < len; i++) {
            int t = list.get(i);
            for (int j = t; j <= n; j++) {
                // 当不更新 f[j] 的时候，对应了二维表示中的 f[i - 1][j]

                // 可以更新 f[j] 的前提是：剩余的 j - k * t 也能够被凑出
                // 更新 f[j] 所依赖的 f[j - t] 对应了二维表示中的 f[i - 1][j - k * t]
                if (f[j - t] != INF) f[j] = Math.min(f[j], f[j - t] + 1);
            }
        }

        return f[n];
    }
}
