package com.meng.specialSubject.knapsack.knapsack;

/**
 *有 N件物品和一个容量是 V的背包。每件物品有且只有一件。
 * 第 i 件物品的体积是v[i] ，价值是 w[i]。
 * 求解将哪些物品装入背包，可使这些物品的总体积不超过背包容量，且总价值最大。
 */
public class Q001Knapsack01 {
    /**
     * 递归
     * @param N
     * @param V
     * @param v
     * @param w
     * @return
     */
    int res = 0;
    public int maxValueDfs(int N, int V, int[] v, int[] w) {
        dfs(v,w,0,V,0);
        return res;
    }

    private void dfs(int[] v, int[] w, int index, int remainV, int sum) {
        if(index >= v.length){
            res = Math.max(res, sum);
            return;
        }
        //不存放当前物品
        dfs(v,w,index+1,remainV,sum);
        //存放当前物品
        if (v[index] <=remainV){
            dfs(v,w,index+1,remainV-v[index],sum+w[index]);
        }
    }

    /**
     *  动态规划
     * @param N
     * @param V
     * @param v
     * @param w
     * @return
     */
    public int maxValueDp(int N, int V, int[] v, int[] w) {
       int[][] dp = new int[N][V+1];
       for(int i = v[0] ; i<=V ; i++){
           dp[0][i] = w[0];
       }
       for(int i = 1; i < N ; i++){
           for(int j = 0; j <= V ; j++){
               dp[i][j] = dp[i-1][j];
               if (j >= v[i]) {
                   dp[i][j] = Math.max(dp[i][j], dp[i-1][j-v[i]] + w[i]);
               }
           }
       }
       return  dp[N-1][V];
    }
    public int maxValueDp2(int N, int V, int[] v, int[] w) {
        int[][] dp = new int[2][V+1];
        for(int i = v[0] ; i<=V ; i++){
            dp[0][i] = w[0];
        }
        for(int i = 1; i < N ; i++){
            for(int j = 0; j <= V ; j++){
                dp[i & 1][j] = dp[(i - 1) & 1][j];
                if (j >= v[i]) {
                    dp[i & 1][j] = Math.max(dp[i & 1][j], dp[(i - 1) & 1][j - v[i]] + w[i]);
                }
            }
        }
        return  dp[(N-1)&1][V];
    }
    public int maxValueDp3(int N, int V, int[] v, int[] w) {
        int[]dp = new int[V+1];
        for(int i = 0; i < N ; i++){
            for(int j = V ; j >= v[i] ; j--){
                dp[j] = Math.max(dp[j],dp[j-v[i]]+w[i]);
            }
        }
        return  dp[V];
    }
    public static void main(String[] args) {
        int N = 30;
        int V = 100;
        int[] v = {8, 15, 23, 30, 12, 25, 16, 7, 20, 11, 9, 14, 19, 5, 22, 27, 13, 6, 17, 29, 24, 10, 3, 18, 21, 26, 4, 28, 2, 1};
        int[] w = {10, 20, 25, 35, 15, 30, 20, 10, 28, 18, 12, 22, 26, 8, 27, 32, 16, 9, 21, 34, 29, 14, 5, 23, 30, 31, 7, 33, 3, 2};
        Q001Knapsack01 demo = new Q001Knapsack01();
        System.out.println(demo.maxValueDfs(N,V,v,w));
        System.out.println(demo.maxValueDp(N,V,v,w));
        System.out.println(demo.maxValueDp2(N,V,v,w));
        System.out.println(demo.maxValueDp3(N,V,v,w));
    }
}
