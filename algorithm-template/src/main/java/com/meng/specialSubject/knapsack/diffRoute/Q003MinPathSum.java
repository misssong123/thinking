package com.meng.specialSubject.knapsack.diffRoute;
import java.util.ArrayList;
import java.util.List;

public class Q003MinPathSum {
    /**
     * 2
     * ms
     * 击败
     * 95.12%
     * 使用 Java 的用户
     * 消耗内存分布
     * 44.47
     * MB
     * 击败
     * 35.77%
     * 使用 Java 的用户
     * @param grid
     * @return
     */
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for(int i= 1;i < m;i++){
            dp[i][0] = dp[i-1][0] + grid[i][0];
        }
        for (int i = 1; i < n; i++){
            dp[0][i] = dp[0][i-1] + grid[0][i];
        }
        for (int i = 1; i < m; i++){
            for (int j = 1 ; j <  n ; j++){
                dp[i][j] = Math.min(dp[i-1][j],dp[i][j-1]) + grid[i][j];
            }
        }
        return dp[m-1][n-1];
    }

    public static void main(String[] args) {
        Q003MinPathSum q003MinPathSum = new Q003MinPathSum();
        int[][] grid = {{1,3,1,4},{1,4,5,1},{4,4,2,1},{4,5,6,7},{2,1,2,3}};
        System.out.println(q003MinPathSum.minPathSum(grid));
        System.out.println(q003MinPathSum.printMinPathSum(grid));
        Solution solution = new Solution();
        System.out.println(solution.minPathSum(grid));
    }
    public String printMinPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        int[] path = new int[m * n];
        dp[0][0] = grid[0][0];
        for(int i= 1;i < m;i++){
            dp[i][0] = dp[i-1][0] + grid[i][0];
            path[i * n] = (i-1) * n;
        }
        for (int i = 1; i < n; i++){
            dp[0][i] = dp[0][i-1] + grid[0][i];
            path[i] = i - 1;
        }
        for (int i = 1; i < m; i++){
            for (int j = 1 ; j <  n ; j++){
                dp[i][j] = Math.min(dp[i-1][j],dp[i][j-1]) + grid[i][j];
                if (dp[i-1][j] < dp[i][j-1]){
                    path[i * n + j] = (i-1)*n+j;
                }else {
                    path[i * n + j] = i * n + j - 1;
                }
            }
        }
        List<String> list = new ArrayList<>(m+n-1);
        int index = m + n -2,lastIndex= path[m * n-1];
        list.add("("+(m-1)+","+(n-1)+")");
        index--;
        while (index>=0){
            list.add(0,"("+(lastIndex/n)+","+(lastIndex%n)+")");
            lastIndex = path[lastIndex];
            index--;
        }
        return String.join("->",list);
    }
}
class Solution {
    int m, n;
    public int minPathSum(int[][] grid) {
        m = grid.length;
        n = grid[0].length;
        int[][] f = new int[m][n];
        int[] g = new int[m * n];
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (i == m - 1 && j == n - 1) {
                    f[i][j] = grid[i][j];
                } else {
                    int bottom = i + 1 < m ? f[i + 1][j] + grid[i][j] : Integer.MAX_VALUE;
                    int right  = j + 1 < n ? f[i][j + 1] + grid[i][j] : Integer.MAX_VALUE;
                    f[i][j] = Math.min(bottom, right);
                    g[getIdx(i, j)] = bottom < right ? getIdx(i + 1, j) : getIdx(i, j + 1);
                }
            }
        }

        int idx = getIdx(0,0);
        for (int i = 1; i <= m + n; i++) {
            if (i == m + n) continue;
            int x = parseIdx(idx)[0], y = parseIdx(idx)[1];
            System.out.print("(" + x + "," + y + ") ");
            idx = g[idx];
        }
        System.out.println(" ");

        return f[0][0];
    }
    int[] parseIdx(int idx) {
        return new int[]{idx / n, idx % n};
    }
    int getIdx(int x, int y) {
        return x * n + y;
    }
}
