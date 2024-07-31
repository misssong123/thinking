package com.meng.specialSubject.knapsack.knapsack;
import java.util.Arrays;

/**
 * 分组背包问题
 * 给定N个物品组，和容量为C的背包。
 * 第i个物品组共有s[i]件物品，其中第i组的第j件物品的成本为v[i][j] ，价值为w[i][j] 。
 * 每组有若干个物品，同一组内的物品最多只能选一个。
 * 求解将哪些物品装入背包可使这些物品的费用总和不超过背包容量，且价值总和最大。
 * 示例：
 * 输入：N = 2, C = 9, S = [2, 3], v = [[1,2,-1],[1,2,3]], w = [[2,4,-1],[1,3,6]]
 * 输出：10
 */
public class Q012MaxValueGroup {
    public int maxValueTwo(int N, int C, int[] S, int[][] v, int[][] w) {
        int[][] dp = new int[N + 1][C + 1];
        for(int i = 1; i <= N ; i++){
            for(int j = 1 ; j <= C ; j++){
                for(int k =0 ; k < S[i-1] ; k++){
                    int v1 = v[i-1][k];
                    int w1 = w[i-1][k];
                    if(j >= v1){
                        dp[i][j] = Math.max(dp[i-1][j], dp[i-1][j-v1] + w1);
                    }
                }
            }
            System.out.println(Arrays.toString(dp));
        }

        return dp[N][C];
    }
    public int maxValue(int N, int C, int[] S, int[][] v, int[][] w) {
        int[] dp = new int[C + 1];
        for(int i = 0; i < N ; i++){
            for(int j = C ; j >=0 ; j--){
                for(int k =0 ; k < S[i] ; k++){
                    int v1 = v[i][k];
                    int w1 = w[i][k];
                    if(j >= v1){
                        dp[j] = Math.max(dp[j], dp[j-v1] + w1);
                    }
                }
            }
        }
        return dp[C];
    }

    public static void main(String[] args) {
        Q012MaxValueGroup demo = new Q012MaxValueGroup();
        int N = 2 , C = 9;
        int[] S = {2, 3};
        int[][] v = {{1,2,-1},{1,3,3}};
        int[][] w = {{2,4,-1},{1,6,6}};
        System.out.println(demo.maxValue(N, C, S, v, w));
    }
}
