package com.meng.specialSubject.knapsack.knapsack;

public class Q008maxValueBinary {
    public int maxValue(int N, int C, int[] s, int[] v, int[] w) {
        int[] dp = new int[C + 1];
        for (int i = 0; i < N; i++) {
            if (v[i] > C){
                continue;
            }
            int num = s[i];
            for(int j = i ; num > 0 ; j<<=1){
                int count = Math.min(num,j);
                int v1 = v[i] * count;
                int w1 = w[i] * count;
                for(int k = C ; k >= v1 ; k--){
                    dp[k] = Math.max(dp[k],dp[k - v1] + w1);
                }
                num-= count;
            }
        }
        return dp[C];
    }
}
