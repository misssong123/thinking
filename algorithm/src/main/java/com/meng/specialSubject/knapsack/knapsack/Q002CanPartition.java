package com.meng.specialSubject.knapsack.knapsack;

import java.util.Arrays;

public class Q002CanPartition {
    /**
     * 执行用时分布
     * 22
     * ms
     * 击败
     * 76.72%
     * 复杂度分析
     * 消耗内存分布
     * 41.43
     * MB
     * 击败
     * 53.72%
     * @param nums
     * @return
     */
    public boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum% 2 != 0){
            return false;
        }
        int max = sum /2 ;
        int[] cache = new int[max + 1];
        for(int num : nums){
            if (num > max){
                return false;
            }
            if (num == max){
                return true;
            }
            for (int j = max; j >= num; j--){
                cache[j] = Math.max(cache[j], cache[j - num] + num);
            }
        }
        return cache[max] == max;
    }

    /**
     * 执行用时分布
     * 21
     * ms
     * 击败
     * 79.47%
     * 复杂度分析
     * 消耗内存分布
     * 40.96
     * MB
     * 击败
     * 89.88%
     * @param nums
     * @return
     */
    public boolean canPartition2(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum% 2 != 0){
            return false;
        }
        int max = sum /2 ;
        boolean[] dp = new boolean[max + 1];
        dp[0] = true;
        for(int num : nums){
            if (num > max){
                return false;
            }
            if (num == max){
                return true;
            }
            for (int j = max; j >= num; j--){
                dp[j] = dp[j] || dp[j-num];
            }
        }
        return dp[max];
    }
}
