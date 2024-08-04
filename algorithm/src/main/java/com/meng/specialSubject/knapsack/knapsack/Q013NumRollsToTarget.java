package com.meng.specialSubject.knapsack.knapsack;

public class Q013NumRollsToTarget {
    /**
     * 执行用时分布
     * 23
     * ms
     * 击败
     * 30.42%
     * 复杂度分析
     * 消耗内存分布
     * 40.85
     * MB
     * 击败
     * 39.72%
     * @param n
     * @param m
     * @param t
     * @return
     */
    public int numRollsToTargetTwo(int n, int m, int t) {
        int[][] dp = new int[n+1][t+1];
        dp[0][0] = 1;
        for(int i = 1 ; i <= n ; i++){
            for(int j = 1; j <= t ; j++){
                for(int k = 1 ; k <= m ; k++){
                    if (j>=k){
                        dp[i][j] = (dp[i][j]+ dp[i-1][j-k]) % mod;
                    }
                }
            }
        }
        return  dp[n][t];
    }
    int mod = (int)1e9+7;

    /**
     * 执行用时分布
     * 24
     * ms
     * 击败
     * 16.62%
     * 复杂度分析
     * 消耗内存分布
     * 39.59
     * MB
     * 击败
     * 74.37%
     * @param n
     * @param m
     * @param t
     * @return
     */
    public int numRollsToTarget(int n, int m, int t) {
        int[] dp = new int[t+1];
        dp[0] = 1;
        for(int i = 1 ; i <= n ; i++){
            for(int j = t; j >=0 ; j--){
                dp[j] = 0;
                for(int k = 1 ; k <= m ; k++){
                    if (j>=k){
                        dp[j] = (dp[j]+ dp[j-k]) % mod;
                    }
                }
            }
        }
        return  dp[t];
    }
}
