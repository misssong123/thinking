package com.meng.specialSubject.sort;

public class Sort07MergeSort {
    //归并排序
    public void mergeSort(int[] arr){
        mergeSort(arr,0,arr.length -1);
    }

    private void mergeSort(int[] arr, int start, int end) {
        if (start >= end){
            return;
        }
        int mid = start + (end - start) / 2;
        mergeSort(arr,start,mid);
        mergeSort(arr,mid + 1,end);
        //合并
        merge(arr,start,mid,end);
    }

    private void merge(int[] arr, int start, int mid, int end) {
        int[] temp =  new int[end - start + 1];
        int index = 0;
        int l = start,r = mid + 1;
        while (l <= mid && r<= end){
            if (arr[l] <= arr[r]){
                temp[index++] =arr[l++];
            }else{
                temp[index++] =arr[r++];
            }
        }
        while (l <= mid){
            temp[index++] =arr[l++];
        }
        while (r <= end){
            temp[index++] =arr[r++];
        }
        System.arraycopy(temp, 0, arr, start, temp.length);
    }

}
