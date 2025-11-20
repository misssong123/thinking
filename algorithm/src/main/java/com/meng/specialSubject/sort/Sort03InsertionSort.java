package com.meng.specialSubject.sort;
/**
 *
 * 类似于人们在整理手中的卡片时的做法，
 * 从数组的第二个元素开始，逐个插入已排序部分的适当位置。
 */
public class Sort03InsertionSort {
    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int temp = arr[i];
            int j = binarySearch(arr, 0, i - 1, temp);
            for (int k = i - 1; k >= j; k--) {
                arr[k + 1] = arr[k];
            }
            arr[j] = temp;
        }
    }

    private static int binarySearch(int[] arr, int low, int high, int key) {
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == key) {
                return mid;
            } else if (arr[mid] < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }
    public void insertSort(int[] arr){
        for(int i = 1 ; i < arr.length ; i++){
            int num = arr[i];
            int index = i - 1;
            while (index >= 0 && arr[index] > num){
                arr[index+1] = arr[index];
                index--;
            }
            arr[index+1] = num;
        }
    }
    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 4, 2};
        insertionSort(arr);
        for (int i : arr)
            System.out.print(i + " ");
    }
}
