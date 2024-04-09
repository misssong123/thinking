package com.meng.specialSubject.search.binary_search;

/**
 * 从非降序数组中查找指定元素
 */
public class BinarySearch01 {
    public static void main(String[] args) {
        int[] nums =  {1,1,1,2,2,2,2,2,2,2,2,2,2,2,4,5,6,8,9,10};
        int target = 7;
        System.out.println(targetValFirstIndex(nums,target));
        System.out.println(targetValEndIndex(nums,target));
        System.out.println(lowerBound(nums,target));
        System.out.println(highBound(nums,target));
    }

    /**
     * 指定元素的第一个位置,不存在返回-1
     * @param nums
     * @param val
     * @return
     */
    public static int targetValFirstIndex(int[] nums, int val) {
        int left = 0, right = nums.length-1;
        int ans = -1;
        while (left <= right) {
            int mid = left + (right - left)/ 2;
            if (nums[mid]>= val){
                ans = nums[mid] == val?mid:ans;
                right = mid - 1;
            }else {
                left = mid + 1;
            }
        }
        return ans;
    }
    /**
     * 指定元素的最后一个位置,不存在返回-1
     * @param nums
     * @param val
     * @return
     */
    public static int targetValEndIndex(int[] nums, int val) {
        int left = 0, right = nums.length-1;
        int ans = -1;
        while (left <= right) {
            int mid = left + (right - left)/ 2;
            if (nums[mid] <= val){
                ans = nums[mid] == val?mid:ans;
                left = mid + 1;
            }else {
                right = mid - 1;
            }
        }
        return ans;
    }

    /**
     * 小于指定元素的第一个位置,不存在返回-1
     * @param nums
     * @param val
     * @return
     */
    public static int lowerBound(int[] nums, int val) {
        int left = 0, right = nums.length-1;
        while (left <= right) {
            int mid = left + (right - left)/ 2;
            if (nums[mid]>=val){
                right = mid - 1;
            }else {
                left = mid+1;
            }
        }
        return right;
    }

    /**
     *  大于等于指定元素的第一个位置,不存在返回-1
     * @param nums
     * @param val
     * @return
     */
    public static int highBound(int[] nums, int val) {
        int left = 0, right = nums.length-1;
        while (left <= right) {
            int mid = left + (right - left)/ 2;
            if (nums[mid]>val){
                right = mid - 1;
            }else {
                left = mid+1;
            }
        }
        return left==nums.length?-1:left;
    }
}
