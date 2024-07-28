package com.meng.specialSubject.knapsack.knapsack;

public class Q009maxValueQueue {
    public int maxValue(int N, int C, int[] s, int[] v, int[] w) {
        int[] dp = new int[C + 1];
        int[] g = new int[C + 1]; // 辅助队列，记录的是上一次的结果
        int[] q = new int[C + 1]; // 主队列，记录的是本次的结果

        // 枚举物品
        for (int i = 0; i < N; i++) {
            int vi = v[i];
            int wi = w[i];
            int si = s[i];

            // 将上次算的结果存入辅助数组中
            g = dp.clone();

            // 枚举余数
            for (int j = 0; j < vi; j++) {
                // 初始化队列，head 和 tail 分别指向队列头部和尾部
                int head = 0, tail = -1;
                // 枚举同一余数情况下，有多少种方案。
                // 例如余数为 1 的情况下有：1、vi + 1、2 * vi + 1、3 * vi + 1 ...
                for (int k = j; k <= C; k+=vi) {
                    dp[k] = g[k];
                    // 将不在窗口范围内的值弹出
                    if (head <= tail && q[head] < k - si * vi) head++;
                    // 如果队列中存在元素，直接使用队头来更新
                    if (head <= tail) dp[k] = Math.max(dp[k], g[q[head]] + (k - q[head]) / vi * wi);
                    // 当前值比对尾值更优，队尾元素没有存在必要，队尾出队
                    while (head <= tail && g[q[tail]] - (q[tail] - j) / vi * wi <= g[k] - (k - j) / vi * wi) tail--;
                    // 将新下标入队
                    q[++tail] = k;
                }
            }
        }
        return dp[C];
    }
}
