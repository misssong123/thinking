package com.meng.specialSubject.sort;


import java.util.Arrays;

/**
 * 插入排序的一种变体，通过比较相隔一定间隔的元素进行排序，
 * 间隔逐渐减小直到为1，最终成为简单的插入排序。
 * 下面是希尔排序的基本步骤：
 * 1.选择间隔（增量）：首先选择一个间隔（或增量）h，这个间隔通常会从数组长度的一半开始，
 * 并且每次迭代减少间隔，常见的递减规则有h=h/2，直至h=1。
 * 2.子序列排序：对所有相隔h的元素子序列进行插入排序，即对下标为0, h, 2h, 3h... 的元素进行排序；
 * 然后对下标为1, h+1, 2h+1... 的元素进行排序；如此类推，直到所有子序列排序完成。
 * 3.减少间隔，重复排序：将间隔h减半，然后对新的子序列进行排序，直到间隔h降为1。
 * 4.最后一次排序：当h=1时，进行一次完整的插入排序，此时整个数组接近有序，排序效率高
 */
public class Sort04ShellSort {
    public static void shellSort(int[] arr){
        int gap = arr.length/2;
        while (gap >0){
            //间隔为gap的插入排序
            for(int i = 0 ; i < gap ; i++){
                for(int j = i + gap ; j < arr.length ; j+=gap){
                    int index = j;
                    while (index-gap >=0 && arr[index -gap] > arr[index]){
                        int temp = arr[index];
                        arr[index] = arr[index-gap];
                        arr[index-gap] = temp;
                        index = index-gap;
                    }
                }
            }
            gap = gap/2;
        }
    }
    public static void shellSortOptimize(int[] arr) {
        int n = arr.length;
        int h = 1;
        while (h < n / 3) {
            h = 3 * h + 1; // Sedgewick序列
        }
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && arr[j] < arr[j - h]; j -= h) {
                    int temp = arr[j];
                    arr[j] = arr[j - h];
                    arr[j - h] = temp;
                }
            }
            h /= 3; // 下一个间隔
        }
    }

    public static void main(String[] args) {
        int[] arr = {9,8,7,6,5,4,3,2,1};
        shellSort(arr);
        for (int i : arr)
            System.out.print(i+" ");
    }
}
