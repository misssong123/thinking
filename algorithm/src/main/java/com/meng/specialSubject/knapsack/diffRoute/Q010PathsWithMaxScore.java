package com.meng.specialSubject.knapsack.diffRoute;

import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 上的「1301. 最大得分的路径数目」
 * 给你一个正方形字符数组 board ，你从数组最右下方的字符 'S' 出发。
 * 你的目标是到达数组最左上角的字符 'E' ，数组剩余的部分为数字字符 1,2,...,9 或者障碍 'X'。
 * 在每一步移动中，你可以向上、向左或者左上方移动，可以移动的前提是到达的格子没有障碍。
 * 一条路径的 「得分」 定义为：路径上所有数字的和。
 * 请你返回一个列表，包含两个整数：第一个整数是 「得分」 的最大值，
 * 第二个整数是得到最大得分的方案数，请把结果对 10^9 + 7 取余。
 * 如果没有任何路径可以到达终点，请返回 [0, 0] 。
 */
public class Q010PathsWithMaxScore {
    int N = 1000000007;
    /**
     * 17
     * ms
     * 击败
     * 28.10%
     * 复杂度分析
     * 消耗内存分布
     * 43.84
     * MB
     * 击败
     * 33.89%
     * @param board
     * @return
     */
    public int[] pathsWithMaxScore(List<String> board) {
        int m = board.size(),n = board.get(0).length();
        int[][] path = {{1,0},{0,1},{1,1}};
        int[][][] dp = new int[m][n][2];
        //初始化
        dp[m-1][n-1][0] = 0;
        dp[m-1][n-1][1] = 1;
        for (int i = m - 1 ; i >= 0 ; i --){
            for(int j = n -1 ; j >= 0 ; j--){
                if (i == m-1 && j == n-1){
                    continue;
                }
                //障碍直接跳过
                if (board.get(i).charAt(j) == 'X'){
                    continue;
                }
                char c = board.get(i).charAt(j);
                int num = c == 'E' ? 0 : c - '0';
                int maxNum = 0;
                int maxCount = 0;
                for (int k = 0 ; k < 3 ; k ++){
                    int x = i + path[k][0],y = j + path[k][1];
                    if (x < 0 || x >= m || y < 0 || y >= n ){
                        continue;
                    }
                    if (dp[x][y][0] >= maxNum){
                        maxCount = dp[x][y][1] + (dp[x][y][0] == maxNum ? maxCount : 0);
                        maxNum = dp[x][y][0];
                        maxCount = maxCount % N;
                    }
                }
                if (maxCount >0){
                    dp[i][j][0] = maxNum + num;
                    dp[i][j][1] = maxCount;
                }
            }
        }
        return dp[0][0];
    }

    public static void main(String[] args) {
        Q010PathsWithMaxScore demo = new Q010PathsWithMaxScore();
        List<String> board = Arrays.asList("E23","2X2","12S");
        System.out.println(Arrays.toString(demo.pathsWithMaxScore(board)));
    }

}
