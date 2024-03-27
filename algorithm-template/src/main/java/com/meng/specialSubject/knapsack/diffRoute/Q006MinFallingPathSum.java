package com.meng.specialSubject.knapsack.diffRoute;

import java.util.Arrays;

/**
 * 这是 LeetCode 上的「1289. 下降路径最小和  II」，难度为 Hard。
 * 给你一个整数方阵 arr ，定义「非零偏移下降路径」为：从 arr 数组中的每一行选择一个数字，
 * 且按顺序选出来的数字中，相邻数字不在原数组的同一列。
 * 请你返回非零偏移下降路径数字和的最小值。
 */
public class Q006MinFallingPathSum {
    /**
     * 执行用时分布
     * 9
     * ms
     * 击败
     * 57.14%
     * 使用 Java 的用户
     * 消耗内存分布
     * 50.92
     * MB
     * 击败
     * 5.36%
     * 使用 Java 的用户
     * @param grid
     * @return
     */
    public int minFallingPathSum(int[][] grid) {
        int firstMinNum = Integer.MAX_VALUE,firstMinIndex = 0,secondMinNum = Integer.MAX_VALUE;
        int currentFirstMinNum = Integer.MAX_VALUE,currentFirstMinIndex = 0,currentSecondMinNum = Integer.MAX_VALUE;
        int n = grid.length;
        for(int i = 0 ; i < n ; i++){
            if (firstMinNum >= grid[0][i]){
                secondMinNum = firstMinNum;
                firstMinIndex = i;
                firstMinNum = grid[0][i];
            }else if (secondMinNum >grid[0][i]){
                secondMinNum = grid[0][i];
            }
        }
        System.out.println(firstMinNum +"("+firstMinIndex+ ")," + secondMinNum);
        for (int i = 1 ; i < n ; i++){
            for (int j = 0 ; j < n ; j++){
                if (j == firstMinIndex){
                    grid[i][j]+= secondMinNum;
                }else {
                    grid[i][j]+= firstMinNum;
                }
                if (currentFirstMinNum >= grid[i][j]){
                    currentSecondMinNum = currentFirstMinNum;
                    currentFirstMinIndex = j;
                    currentFirstMinNum = grid[i][j];
                }else if (currentSecondMinNum >grid[i][j]){
                    currentSecondMinNum = grid[i][j];
                }
            }
            secondMinNum = currentSecondMinNum;
            firstMinIndex = currentFirstMinIndex;
            firstMinNum = currentFirstMinNum;
            currentFirstMinNum = Integer.MAX_VALUE;
            currentSecondMinNum = Integer.MAX_VALUE;
            System.out.println(Arrays.toString(grid[i]));
            System.out.println(firstMinNum +"("+firstMinIndex+ ")," + secondMinNum);
        }
        return Arrays.stream(grid[n - 1]).min().getAsInt();
    }

    /**
     * 执行用时分布
     * 1
     * ms
     * 击败
     * 100.00%
     * 使用 Java 的用户
     * 消耗内存分布
     * 48.69
     * MB
     * 击败
     * 92.41%
     * 使用 Java 的用户
     * @param grid
     * @return
     */
    public int minFallingPathSumOfficial(int[][] grid) {
        int n = grid.length;
        int first_min_sum = 0;
        int second_min_sum = 0;
        int first_min_index = -1;

        for (int i = 0; i < n; i++) {
            int cur_first_min_sum = Integer.MAX_VALUE;
            int cur_second_min_sum = Integer.MAX_VALUE;
            int cur_first_min_index = -1;

            for (int j = 0; j < n; j++) {
                int cur_sum = (j != first_min_index ? first_min_sum : second_min_sum) + grid[i][j];
                if (cur_sum < cur_first_min_sum) {
                    cur_second_min_sum = cur_first_min_sum;
                    cur_first_min_sum = cur_sum;
                    cur_first_min_index = j;
                } else if (cur_sum < cur_second_min_sum) {
                    cur_second_min_sum = cur_sum;
                }
            }
            first_min_sum = cur_first_min_sum;
            second_min_sum = cur_second_min_sum;
            first_min_index = cur_first_min_index;
        }
        return first_min_sum;
    }
    public static void main(String[] args) {
        Q006MinFallingPathSum demo = new Q006MinFallingPathSum();
        int[][] grids = {{-73,61,43,-48,-36},{3,30,27,57,10},{96,-76,84,59,-15},{5,-49,76,31,-7},{97,91,61,-46,67}};
        System.out.println(demo.minFallingPathSum(grids));
    }
}
