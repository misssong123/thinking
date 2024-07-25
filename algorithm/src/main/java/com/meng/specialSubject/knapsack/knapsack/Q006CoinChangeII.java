package com.meng.specialSubject.knapsack.knapsack;


public class Q006CoinChangeII {
    /**
     * 执行用时分布
     * 2
     * ms
     * 击败
     * 100.00%
     * 复杂度分析
     * 消耗内存分布
     * 40.06
     * MB
     * 击败
     * 68.54%
     * @param cnt
     * @param cs
     * @return
     */
    public int change(int cnt, int[] cs) {
        int[] dp = new int[cnt+1];
        dp[0] =1;
        for (int c : cs){
            if (c > cnt){
                continue;
            }
            for(int i = c; i <= cnt; i++){
                dp[i] += dp[i-c];
            }
        }
        return  dp[cnt];
    }
}
