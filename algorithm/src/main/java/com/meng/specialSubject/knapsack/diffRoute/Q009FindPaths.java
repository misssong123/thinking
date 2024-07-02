package com.meng.specialSubject.knapsack.diffRoute;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

/**
 * 给你一个大小为 m x n 的网格和一个球。球的起始坐标为 [startRow, startColumn] 。你可以将球移到在
 * 四个方向上相邻的单元格内（可以穿过网格边界到达网格之外）。你 最多 可以移动 maxMove 次球。
 * 给你五个整数 m、n、maxMove、startRow 以及 startColumn ，找出并返回可以将球移出边界的路径数量。
 * 因为答案可能非常大，返回对 109 + 7 取余 后的结果。
 */
public class Q009FindPaths {
    /**
     *
     * 执行用时分布
     * 3
     * ms
     * 击败
     * 99.11%
     * 复杂度分析
     * 消耗内存分布
     * 41.09
     * MB
     * 击败
     * 54.42%
     * @param m
     * @param n
     * @param maxMove
     * @param startRow
     * @param startColumn
     * @return
     */
    int[][] dirs = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    int[][] cache ;
    public int findPathsDfs(int m, int n, int maxMove, int startRow, int startColumn) {
        cache = new int[m*n][maxMove+1];
        for (int[] row : cache) {
            Arrays.fill(row,-1);
        }
        return dfs(m,n,maxMove,startRow,startColumn)% N;
    }

    private int dfs(int m, int n, int maxMove, int x, int y) {
        //不可能走到
        if(x >= maxMove && y >= maxMove&& x + maxMove < m && y + maxMove < n){
            return 0;
        }
        //已经有了相同的路径
        if (cache[x*n+y][maxMove] != -1){
            return cache[x*n+y][maxMove];
        }
        int ans = 0;
        for (int[] dir : dirs) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX < 0 || newX >= m || newY < 0 || newY >= n) {
                ans++;
                continue;
            }
            ans = (ans + dfs(m, n, maxMove - 1, newX, newY)) % N;
        }
        cache[x*n+y][maxMove] = ans % N;
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(new Q009FindPaths().findPaths(1, 3, 3, 0, 1));
        System.out.println(new Q009FindPaths().findPathsOther(1, 3, 3, 0, 1));
    }
    int N =  1000000007;

    /**
     * 执行用时分布
     * 11
     * ms
     * 击败
     * 27.88%
     * 复杂度分析
     * 消耗内存分布
     * 40.88
     * MB
     * 击败
     * 96.90%
     * @param m
     * @param n
     * @param maxMove
     * @param startRow
     * @param startColumn
     * @return
     */
    public int findPaths(int m, int n, int maxMove, int startRow, int startColumn) {
        int[][] dp = new int[m*n][maxMove+1];
        int[][] dirs = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        //初始化
        // 初始化边缘格子的路径数量
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0) add(i*n+j, dp,maxMove);
                if (i == m - 1) add(i*n+j, dp,maxMove);
                if (j == 0) add(i*n+j, dp,maxMove);
                if (j == n - 1) add(i*n+j, dp,maxMove);
            }
        }
        System.out.println(Arrays.deepToString(dp));
        //规划
        for(int i = 1 ; i <= maxMove ; i++){
            for (int j = 0 ; j < m * n ; j++){
                int x =  j / n;
                int y = j % n;
                for (int[] dir : dirs){
                    int newX = x + dir[0];
                    int newY = y + dir[1];
                    if (newX >= 0 && newX < m && newY >= 0 && newY < n) {
                        dp[j][i] += dp[newX*n+newY][i-1];
                        dp[j][i] = dp[j][i]%N;
                    }
                }
            }
        }
        return  dp[startRow*n+startColumn][maxMove];
    }
    // 为每个「边缘」格子，添加一条路径
    void add(int idx, int[][] f,int move) {
        for (int step = 1; step <= move; step++) {
            f[idx][step]++;
        }
    }

    int mod = (int)1e9+7;
    int m, n, NUM;

    /**
     * 执行用时分布
     * 15
     * ms
     * 击败
     * 13.72%
     * 复杂度分析
     * 消耗内存分布
     * 44.13
     * MB
     * 击败
     * 9.29%
     * @param _m
     * @param _n
     * @param _N
     * @param _i
     * @param _j
     * @return
     */
    public int findPathsOther(int _m, int _n, int _N, int _i, int _j) {
        m = _m; n = _n; NUM = _N;

        // f[i][j] 代表从 idx 为 i 的位置出发，移动步数不超过 j 的路径数量
        int[][] f = new int[m * n][NUM + 1];

        // 初始化边缘格子的路径数量
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0) add(i, j, f);
                if (i == m - 1) add(i, j, f);
                if (j == 0) add(i, j, f);
                if (j == n - 1) add(i, j, f);
            }
        }
        // 定义可移动的四个方向
        int[][] dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1}};

        // 从小到大枚举「可移动步数」
        for (int step = 1; step <= NUM; step++) {
            // 枚举所有的「位置」
            for (int k = 0; k < m * n; k++) {
                int x = parseIdx(k)[0], y = parseIdx(k)[1];
                for (int[] d : dirs) {
                    int nx = x + d[0], ny = y + d[1];
                    // 如果位置有「相邻格子」，则「相邻格子」参与状态转移
                    if (nx >= 0 && nx < m && ny >= 0 && ny < n) {
                        f[k][step] += f[getIndex(nx, ny)][step - 1];
                        f[k][step] %= mod;
                    }
                }
            }
        }

        // 最终结果为从起始点触发，最大移动步数不超 N 的路径数量
        return f[getIndex(_i, _j)][NUM];
    }

    // 为每个「边缘」格子，添加一条路径
    void add(int x, int y, int[][] f) {
        int idx = getIndex(x, y);
        for (int step = 1; step <= NUM; step++) {
            f[idx][step]++;
        }
    }

    // 将 (x, y) 转换为 index
    int getIndex(int x, int y) {
        return x * n + y;
    }

    // 将 index 解析回 (x, y)
    int[] parseIdx(int idx) {
        return new int[]{idx / n, idx % n};
    }
}
