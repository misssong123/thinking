package com.meng.specialSubject.unionandfind;

import java.util.Arrays;

/**
 * 并查集
 */

/**
 * 1. 查找元素属于哪个集合（找根） 沿着数组表示的树形关系往上一直找到根(即：树中中元素为其下标的位置)
 *
 * 2. 查看两个元素是否属于同一个集合 沿着数组表示的树形关系往上一直找到树的根，
 * 如果根相同表明在同一个集合，否则不在
 *
 * 3. 将两个集合归并成一个集合
 *
 * 4. 集合的个数 遍历数组，数组中元素为其下标的个数即为集合的个数
 */
public class UnionFind {
    int[] cache ;
    UnionFind(int n){
        cache = new int[n];
        //初始化
        for (int i = 0; i < n; i++) {
            cache[i] = i;
        }
    }
    public int find(int x){
        int root = x;
        while (cache[root] != root){
            root = cache[root];
        }
        while (x != root){
            //记录父节点
            int parent = cache[x];
            //父节点存放的下标等于根节点
            cache[x] = root;
            //调整当前值
            x = parent;
        }
        return root;
    }
    public void union(int x,int y){
        int rootX = find(x);
        int rootY = find(y);
        if(rootX != rootY){
            if (rootX > rootY){
                cache[rootX] = rootY;
            }else {
                cache[rootY] = rootX;
            }
        }
    }

    public static void main(String[] args) {
        UnionFind uf = new UnionFind(10);
        uf.union(0,3);
        uf.union(0,5);
        uf.union(0,7);
        uf.union(6,8);
        uf.union(6,9);
        uf.union(4,6);
        uf.union(1,4);
        uf.union(1,2);
        uf.union(1,0);
        System.out.println(Arrays.toString(uf.cache));
        uf.find(9);
        System.out.println(Arrays.toString(uf.cache));

    }
}
