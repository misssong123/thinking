package com.meng.specialSubject.knapsack.knapsack;

/**
 * 多重背包问题
 * 有N种物品和一个容量为C的背包，每种物品「数量有限」。
 * 第i件物品的体积是v[i]，价值是w[i]，数量为s[i]。
 * 问选择哪些物品，每件物品选择多少件，可使得总价值最大。
 * 其实就是在 0-1 背包问题的基础上，增加了每件物品可以选择「有限次数」的特点（在容量允许的情况下）。
 */
public class Q007KnapsackMany {
    public int maxValue(int N, int C, int[] s, int[] v, int[] w) {
        int[] dp = new int[C + 1];
        for (int i = 0; i < N; i++) {
            if (v[i] > C){
                continue;
            }
            for (int j = 0 ; j < s[i] ; j++){
                for(int k = C ; k >=v[i] ; k--){
                    dp[k] = Math.max(dp[k],dp[k - v[i]] + w[i]);
                }
            }
        }
        return dp[C];
    }
    public static int maxValueAi(int N, int C, int[] s, int[] v, int[] w) {
        int[] dp = new int[C + 1];

        for (int i = 0; i < N; i++) {
            int num = s[i];
            for (int k = 1; num > 0; k <<= 1) {
                int count = Math.min(k, num);
                num -= count;
                int vi = v[i] * count;
                int wi = w[i] * count;
                for (int j = C; j >= vi; j--) {
                    dp[j] = Math.max(dp[j], dp[j - vi] + wi);
                }
            }
        }

        return dp[C];
    }

    public static void main(String[] args) {
        Q007KnapsackMany demo = new Q007KnapsackMany();
        System.out.println(demo.maxValue(3, 5, new int[]{2,3,2}, new int[]{1,2,3}, new int[]{4,5,6}));
    }
}
