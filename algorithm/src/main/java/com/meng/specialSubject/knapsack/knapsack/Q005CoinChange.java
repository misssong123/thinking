package com.meng.specialSubject.knapsack.knapsack;

import java.util.Arrays;

public class Q005CoinChange {
    /**
     * 执行用时分布
     * 11
     * ms
     * 击败
     * 97.90%
     * 复杂度分析
     * 消耗内存分布
     * 43.07
     * MB
     * 击败
     * 89.46%
     * @param cs
     * @param cnt
     * @return
     */
    public int coinChange(int[] cs, int cnt) {
        if (cnt == 0){
            return 0;
        }
        int[] dp = new int[cnt+1];
        //赋值默认值
        Arrays.fill(dp,cnt+1);
        for(int c : cs){
            if (c > cnt){
                continue;
            }
            dp[c] = 1;
            for(int i = c + 1 ; i <= cnt ; i++){
                dp[i] = Math.min(dp[i-c] +1 ,dp[i]);
            }
        }
        return dp[cnt] > cnt ? -1 : dp[cnt];
    }
}
