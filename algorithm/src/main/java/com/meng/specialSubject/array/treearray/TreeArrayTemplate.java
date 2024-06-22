package com.meng.specialSubject.array.treearray;

import java.util.Arrays;

public class TreeArrayTemplate {
    public static void main(String[] args) {
        int[] nums = {1,3,5,2,4,7};
        NumArray demo = new NumArray(nums);
        demo.update(2,10);
        System.out.println(demo.sumRange(0,2));
        System.out.println(demo.sumRange(3,4));
        demo.update(3,11);
        System.out.println(demo.sumRange(2,4));
        System.out.println(demo.sumRange(0,2));

    }
}

class NumArray {
    int[] tree;
    int lowBit(int x) {
        return x & -x;
    }
    int query(int x) {
        int ans = 0;
        for (int i = x; i > 0; i -= lowBit(i)) {
            ans += tree[i];
        }
        return ans;
    }
    void add(int x, int u) {
        for (int i = x; i <= n; i += lowBit(i)) {
            tree[i] += u;
        }
    }

    int[] nums;
    int n;
    public NumArray(int[] _nums) {
        nums = _nums;
        n = nums.length;
        tree = new int[n + 1];
        for (int i = 0; i < n; i++) {
            add(i + 1, nums[i]);
        }
        System.out.println(Arrays.toString(tree));
    }

    public void update(int i, int val) {
        add(i + 1, val - nums[i]);
        nums[i] = val;
    }

    public int sumRange(int l, int r) {
        return query(r + 1) - query(l);
    }
}
