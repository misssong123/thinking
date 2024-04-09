package com.meng.specialSubject.search.binary_search;

/**
 * 从指定升序数组中查找指定元素,不存在返回-1，反之返回对应下标
 */
public class BinarySearch01 {
    public static void main(String[] args) {
        int[]  nums = {1,2,3,4,5,6,7,8,9};
        System.out.println(binarySearch(nums,10));
    }
    public static int binarySearch(int[] nums, int target) {
        if (nums==null || nums.length==0){
            return -1;
        }
        int left = 0 ,right =  nums.length-1;
        while (left <= right){
            int mid = left + (right-left)/2;
            if (nums[mid] == target){
                return mid;
            }else if (nums[mid] < target){
                left = mid+1;
            }else {
                right = mid-1;
            }
        }
        return -1;
    }
}
