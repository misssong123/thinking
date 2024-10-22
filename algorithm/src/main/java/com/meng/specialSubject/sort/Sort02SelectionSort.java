package com.meng.specialSubject.sort;

/**
 * 算法通过在未排序序列中找到最小（或最大）的元素，
 * 然后将它放到已排序序列的末尾，重复这个过程直到整个序列排序。
 */
public class Sort02SelectionSort {
    public static  void  selectionSort(int[] arr){
        //遍历次数
        for(int i = 0; i < arr.length; i++){
            //遍历数据，记录最小值
            int minIndex = i;
            for(int j = i+1; j < arr.length; j++){
                if (arr[j] < arr[minIndex]){
                    minIndex = j;
                }
            }
            //交换位置
            if (minIndex != i){
                int temp = arr[minIndex];
                arr[minIndex] = arr[i];
                arr[i] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {1,3,5,7,9,2,4,6,8,0};
        selectionSort(arr);
        for (int i : arr)
            System.out.print(i+" ");
    }
}
