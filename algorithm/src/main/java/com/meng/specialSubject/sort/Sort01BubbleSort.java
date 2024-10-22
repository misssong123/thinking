package com.meng.specialSubject.sort;

/**
 * 通过重复遍历数组，比较相邻的元素并交换它们，
 * 直到没有元素需要交换为止。
 */
public class Sort01BubbleSort {
    public void bubbleSort(int[] arr){
        //对比次数
        for(int i = 1 ; i <arr.length ; i++){
            //遍历数组
            for(int j = 0 ; j< arr.length-i ; j++){
                if(arr[j] > arr[j+1]){
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        Sort01BubbleSort sort01BubbleSort = new Sort01BubbleSort();
        int[] arr = {1,3,2,5,4};
        sort01BubbleSort.bubbleSort(arr);
        for (int i : arr)
            System.out.println(i);
    }
}
