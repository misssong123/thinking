package com.meng.specialSubject.array.diffarray;

import java.util.Arrays;

public class DifferenceArray {
    private int[] diff;
    public  DifferenceArray(int[] nums){
        diff = new int[nums.length];
        //初始化差分数组
        diff[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            diff[i] = nums[i] - nums[i-1];
        }
    }
    public void increment(int i,int j,int val){
        diff[i] += val;
        if (j+1 < diff.length){
            diff[j+1] -= val;
        }
    }
    public int[] result(){
        int[] res = new int[diff.length];
        res[0] = diff[0];
        for (int i = 1; i < diff.length; i++) {
            res[i] = res[i-1] + diff[i];
        }
        return res;
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3,4,5};
        DifferenceArray differenceArray = new DifferenceArray(nums);
        differenceArray.increment(1,2,100);
        int[] res = differenceArray.result();
        System.out.println(Arrays.toString(res));
    }
}
