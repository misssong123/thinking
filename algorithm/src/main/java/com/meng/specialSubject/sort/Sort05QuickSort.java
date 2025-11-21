package com.meng.specialSubject.sort;
/**
 * 以下是快速排序的基本步骤：
 * 1.选择基准值（Pivot）：从数组中选择一个元素作为基准值。基准值的选择方法有多种，
 * 可以是数组中的第一个元素、最后一个元素、中间的元素，或者随机选择等。
 * 2.分区（Partition）：重新排列数组，将所有比基准值小的元素放到基准值左边，
 * 所有比基准值大的元素放到基准值右边，这个过程结束后，基准值会处于数组的中间位置，称为分区操作。
 * 3.递归调用：递归地对左右两个子数组重复上述步骤，直到子数组中只包含一个
 * 元素或者没有元素，因为单个元素已经自然“排序”。
 */
public class Sort05QuickSort {
    public static void quickSort(int[] arr) {
        quickSort(arr,0,arr.length-1);
    }
    public static void quickSort(int[] arr,int left,int right) {
        if (left >= right){
            return;
        }
        int index = partition(arr,left,right);
        quickSort(arr,left,index-1);
        quickSort(arr,index+1,right);
    }

    private static int partition(int[] arr, int left, int right) {
        //选择最右边的元素作为基准值
        int pivot = arr[right],r = right;
        while (left < right){
            //从左侧选择大于基准值的元素
            while(left < right && arr[left] < pivot){
                left++;
            }
            //从右侧选择小于基准值的元素
            while (right > left && arr[right] >= pivot){
                right--;
            }
            if (left != right){
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
            }
        }
        //保证基准值在中间
        if (left != r){
            int temp = arr[left];
            arr[left] = arr[r];
            arr[r] = temp;
        }
        return left;
    }

    //快速排序
    public void quickSortOther(int[] arr){
        quickSortOther(arr,0,arr.length -1);
    }

    private void quickSortOther(int[] arr, int start, int end) {
        if (start >= end){
            return;
        }
        int target = arr[start];
        int l = start;
        int r = end;
        while (l < r){
            //从右侧找比target小的元素
            while(r > l && arr[r] >= target){
                r--;
            }
            //从左侧找比target大的元素
            while (l < r && arr[l] <= target){
                l++;
            }
            if (l < r){
                int temp = arr[l];
                arr[l] = arr[r];
                arr[r] = temp;
            }
        }
        // 将基准值放到正确位置
        arr[start] = arr[l];
        arr[l] = target;

        quickSortOther(arr,start,l - 1);
        quickSortOther(arr,l + 1,end);
    }
    public static void main(String[] args) {
        int[] arr = {3,2,1,5,6,4};
        int k = 2;
        System.out.println(findKthLargest(arr,k));
    }

    //快速排序变形，寻找第K大的数据
    public static int findKthLargest(int[] arr, int k) {
        int n = arr.length;
        return quickSelect(arr, 0, n - 1, n - k);
    }
    public static int quickSelect(int[] nums, int l, int r, int k) {
        if (l == r) return nums[k];
        int x = nums[l], i = l - 1, j = r + 1;
        while (i < j) {
            do i++; while (nums[i] < x);
            do j--; while (nums[j] > x);
            if (i < j){
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j] = tmp;
            }
        }
        if (k <= j) return quickSelect(nums, l, j, k);
        else return quickSelect(nums, j + 1, r, k);
    }

}
