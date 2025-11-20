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
    //简单选择排序
    //给定数组：int[] arr={里面n个数据}；第1趟排序，在待排序数据arr[1]~arr[n-1]中选出最小的数据temp，
    // 和其对应的下标position，将它与arr[0]比较，temp小交换就交换arr[0]和arr[position]的位置；
    // 第2趟，在待排序数据arr[2]~arr[n-1]中选出最小的数据temp，和其对应的下标position，
    // 将它与arr[1]比较，temp小交换就交换arr[1]和arr[position]的位置；以此类推，
    // 第i趟在待排序数据arr[i]~arr[n-1]中选出最小的数据temp，将它与arr[i-1]做相似的比较交换，直到全部排序完成。
    public void selectSort(int[] arr){
        for(int i = 0 ; i < arr.length -1 ; i++){
            int minIndex = i;
            for(int j = i + 1 ; j < arr.length ; j++){
                if(arr[j] < arr[minIndex]){
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
    public static void main(String[] args) {
        int[] arr = {1,3,5,7,9,2,4,6,8,0};
        selectionSort(arr);
        for (int i : arr)
            System.out.print(i+" ");
    }
}
