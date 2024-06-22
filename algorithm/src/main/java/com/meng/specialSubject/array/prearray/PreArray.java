package com.meng.specialSubject.array.prearray;

public class PreArray {
    /**
     * 初始化
     * @param nums
     * @return
     */
    int[] nums;
    PreArray(int[] num) {
        int n = num.length;
        nums = new int[n+1];
        for (int i = 0; i < n; i++) {
            nums[i+1] = num[i] + nums[i];
        }
    }
    /**
     * 区间和
     */
    public int sumRange(int i, int j) {
        return  nums[j] - nums[i-1];
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3,4};
        PreArray template = new PreArray(nums);
        System.out.println(template.sumRange(1, 4));
    }
}
