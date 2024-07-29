package com.meng.specialSubject.knapsack.knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定物品数量N和背包容量C。第i件物品的体积是v[i]，价值是w[s] ，可用数量为s[i]：
 * 当s[i]为-1代表是该物品只能用一次
 * 当s[i]为0代表该物品可以使用无限次
 * 当s[i]为任意正整数则代表可用s[i]次
 * 求解将哪些物品装入背包可使这些物品的费用总和不超过背包容量，且价值总和最大。
 */
public class Q010maxValueFix {
    public static void main(String[] args) {
        testCase(3, 5, new int[]{2, 3, 2}, new int[]{1, 2, 3}, new int[]{4, 5, 6}, "Test Case 1");
        testCase(4, 10, new int[]{1, 0, -1, 2}, new int[]{3, 4, 2, 5}, new int[]{8, 7, 6, 9}, "Test Case 2");
        testCase(2, 5, new int[]{0, 1}, new int[]{2, 3}, new int[]{3, 4}, "Test Case 3");
        testCase(4, 8, new int[]{-1, 2, 0, 1}, new int[]{1, 3, 2, 4}, new int[]{5, 6, 3, 7}, "Test Case 4");
        testCase(5, 7, new int[]{1, 0, 2, -1, 3}, new int[]{2, 3, 4, 1, 5}, new int[]{3, 6, 7, 2, 8}, "Test Case 5");
    }

    public static void testCase(int N, int C, int[] s, int[] v, int[] w, String testCaseName) {
        int result = maxValue(N, C, s, v, w);
        int result1 = maxValueMy(N, C, s, v, w);
        System.out.println(testCaseName + ": " + result+"; "+result1);
    }

    public static int maxValueMy(int N, int C, int[] s, int[] v, int[] w) {
        int[] dp = new int[C+1];
        for(int i = 0; i < N ; i++){
            switch (s[i]){
                //01背包
                case -1:
                    for(int j = C; j >= v[i]; j--){
                        dp[j] = Math.max(dp[j], dp[j-v[i]]+w[i]);
                    }
                    break;
                //完全背包
                case 0:
                    for(int j = v[i]; j <=C; j++){
                        dp[j] = Math.max(dp[j], dp[j-v[i]]+w[i]);
                    }
                    break;
                //多重背包
                default:
                    int count = s[i];
                    for(int j = 1 ; count >0 ; j<<=1){
                        int num = Math.min(j, count);
                        int v1 = v[i] * num;
                        int w1 = w[i] * num;
                        count -= num;
                        for(int k = C ; k >= v1 ; k--){
                            dp[k] = Math.max(dp[k], dp[k-v1]+w1);
                        }
                    }
                    break;
            }
        }
        return dp[C];
    }
    public static int maxValue(int N, int C, int[] s, int[] v, int[] w) {
        // 构造出物品的「价值」和「体积」列表
        List<Integer> worth = new ArrayList<>();
        List<Integer> volume = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int type = s[i];

            // 多重背包：应用「二进制优化」转换为 0-1 背包问题
            if (type > 0) {
                for (int k = 1; k <= type; k *= 2) {
                    type -= k;
                    worth.add(w[i] * k);
                    volume.add(v[i] * k);
                }
                if (type > 0) {
                    worth.add(w[i] * type);
                    volume.add(v[i] * type);
                }

                // 01 背包：直接添加
            } else if (type == -1) {
                worth.add(w[i]);
                volume.add(v[i]);

                // 完全背包：对 worth 做翻转进行标记
            } else {
                worth.add(-w[i]);
                volume.add(v[i]);
            }
        }

        // 使用「一维空间优化」方式求解三种背包问题
        int[] dp = new int[C + 1];
        for (int i = 0; i < worth.size(); i++) {
            int wor = worth.get(i);
            int vol = volume.get(i);

            // 完全背包：容量「从小到大」进行遍历
            if (wor < 0) {
                for (int j = vol; j <= C; j++) {
                    // 同时记得将 worth 重新翻转为正整数
                    dp[j] = Math.max(dp[j], dp[j - vol] - wor);
                }

                // 01 背包：包括「原本的 01 背包」和「经过二进制优化的完全背包」
                // 容量「从大到小」进行遍历
            } else {
                for (int j = C; j >= vol; j--) {
                    dp[j] = Math.max(dp[j], dp[j - vol] + wor);
                }
            }
        }
        return dp[C];
    }
}
