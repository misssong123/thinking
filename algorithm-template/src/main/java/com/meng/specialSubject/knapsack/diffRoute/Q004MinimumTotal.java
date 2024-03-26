package com.meng.specialSubject.knapsack.diffRoute;

import java.util.Arrays;
import java.util.List;

/**
 * 给定一个三角形 triangle ，找出自顶向下的最小路径和。
 * 每一步只能移动到下一行中相邻的结点上。
 * 相邻的结点 在这里指的是 下标 与 上一层结点下标 相同或者等于 上一层结点下标 + 1 的两个结点。
 * 也就是说，如果正位于当前行的下标 i ，那么下一步可以移动到下一行的下标 i 或 i + 1 。
 */
public class Q004MinimumTotal {
    /**
     * 2
     * ms
     * 击败
     * 95.96%
     * 使用 Java 的用户
     * 消耗内存分布
     * 42.16
     * MB
     * 击败
     * 93.98%
     * 使用 Java 的用户
     * @param triangle
     * @return
     */
    public int minimumTotal(List<List<Integer>> triangle) {
        /*int index = -1;
        for(List<Integer> list : triangle){
            index++;
            if (index ==0){
                continue;
            }
            for(int i = 0 ; i < list.size() ; i++){
                int t1 = i >= index ? Integer.MAX_VALUE : triangle.get(index-1).get(i);
                int t2 = i - 1 < 0 ? Integer.MAX_VALUE : triangle.get(index-1).get(i-1);
                list.set(i,list.get(i)+Math.min(t1,t2));
            }
        }
        return triangle.get(triangle.size()-1).stream().min(Integer::compareTo).get();*/
        int n = triangle.size();
        int[] f = new int[n];
        f[0] = triangle.get(0).get(0);
        for (int i = 1; i < n; ++i) {
            f[i] = f[i - 1] + triangle.get(i).get(i);
            for (int j = i - 1; j > 0; --j) {
                f[j] = Math.min(f[j - 1], f[j]) + triangle.get(i).get(j);
            }
            f[0] += triangle.get(i).get(0);
        }
        int minTotal = f[0];
        for (int i = 1; i < n; ++i) {
            minTotal = Math.min(minTotal, f[i]);
        }
        return minTotal;
    }

    public static void main(String[] args) {
        Q004MinimumTotal q = new Q004MinimumTotal();
        List<List<Integer>> list = Arrays.asList(Arrays.asList(2), Arrays.asList(3, 4),
                Arrays.asList(6, 5, 7), Arrays.asList(4, 1, 8, 3));
        System.out.println(list);
        System.out.println(q.minimumTotal(list));
        System.out.println(list);

    }
}
