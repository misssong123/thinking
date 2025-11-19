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
     *  大于指定元素的第一个位置,不存在返回-1
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
    /**
     * 查询指定元素首次出现的位置，未存在返回首个大于指定元素的下标
     * @param nums 非递减数组
     * @param target 目标元素
     * @return 首次出现位置或首个大于目标元素的下标
     */
    public static int findFirstOccurrence(int[] nums, int target) {
        int l = 0 ,r = nums.length -1 , res = nums.length;
        while (l <= r){
            int mid = l + (r - l) / 2;
            if (nums[mid] >= target){
                res = mid;
                r = mid - 1;
            }else {
                l = mid + 1;
            }
        }
        return res;
    }

    /**
     * b. 查询指定元素最后出现的位置，未存在返回首个大于指定元素的下标
     * @param nums 非递减数组
     * @param target 目标元素
     * @return 最后出现位置或首个大于目标元素的下标
     */
    public static int findLastOccurrence(int[] nums, int target) {
        int l = 0 , r = nums.length -1 , res = nums.length;
        while (l <= r){
            int mid = l + (r - l) / 2;
            if(nums[mid] > target){
                res = mid;
                r = mid - 1;
            }else{
                l = mid + 1;
            }
        }
        if (res > 0 && nums[res - 1] == target){
            res--;
        }
        return res;
    }
}
