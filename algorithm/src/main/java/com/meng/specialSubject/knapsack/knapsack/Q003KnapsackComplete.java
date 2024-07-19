package com.meng.specialSubject.knapsack.knapsack;

public class Q003KnapsackComplete {
    public int maxValue(int N, int C, int[] v, int[] w) {
        int[] dp = new int[C + 1];
        for(int i = 0 ; i < N ; i++){
            for(int j = i; j<= C ; j++){
                dp[j] = Math.max(dp[j],dp[j-v[i]] + w[i]);
            }
        }
        return  dp[C];
    }
}
