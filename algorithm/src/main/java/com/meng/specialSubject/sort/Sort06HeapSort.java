package com.meng.specialSubject.sort;
/**
 * 它的基本思想是将待排序的序列构建成一个最大堆（或最小堆），然后重复以下步骤：
 * 1.将堆顶元素（即最大或最小元素）与堆的最后一个元素交换。
 * 2.调整堆，使其重新成为一个最大堆（或最小堆），但不包括已经交换到堆尾的元素。
 * 重复步骤1和步骤2，直到堆中只剩下一个元素。
 * 以下是堆排序的详细步骤：
 * 1.构建最大堆：
 * 从最后一个非叶子节点开始，逐个节点进行调整，使每个节点的值都大于其左右子节点的值，从而构建成一个最大堆。
 * 2.排序：
 * 将堆顶元素与堆的最后一个元素交换。
 * 对堆顶元素进行调整，使其重新成为一个最大堆。
 * 重复步骤2，直到堆中只剩下一个元素
 */
public class Sort06HeapSort {
    public static void heapSort(int[] arr) {
        int n = arr.length;
        // 构建最大堆
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        // 排序
        for (int i = n - 1; i > 0; i--) {
            // 交换堆顶元素和最后一个元素
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            // 调整堆
            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i; // 初始化最大元素为根节点
        int left = 2 * i + 1; // 左子节点
        int right = 2 * i + 2; // 右子节点
        // 如果左子节点大于根节点，则更新最大元素
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        // 如果右子节点大于最大元素，则更新最大元素
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }
        // 如果最大元素不是根节点，则交换它们，并递归调整子树
        if (largest!= i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }
    //堆排序
    public void heapSortOther(int[] arr){
        //构建大顶堆
        for(int i = (arr.length -1) /2 ; i >= 0 ; i--){
            judgeHeapOther(arr,i,arr.length);
        }
        //交换顶堆元素
        for(int i = arr.length -1 ; i > 0 ; i--){
            int temp = arr[i];
            arr[i] = arr[0];
            arr[0] = temp;
            judgeHeapOther(arr,0,i);
        }
    }

    private void judgeHeapOther(int[] arr, int parent, int end) {
        int temp = arr[parent];
        for (int child = 2 * parent + 1 ; child < end ; child = 2 * child + 1) {
            if (child + 1 < end && arr[child] < arr[child + 1]) {
                child++;
            }
            if(temp < arr[child]){
                arr[parent] = arr[child];
                parent = child;
            }else {
                break;
            }
        }
        arr[parent] = temp;
    }
    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7,1,2,3,4,5,4,3,2,1,5,5,55,53};
        heapSort(arr);
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }

    /**
     * 寻找第K大的数
     * @param arr
     * @param k
     * @return
     */
    public static int findKthLargest(int[] arr,int k){
        int len = arr.length;
        //构建最大堆
        for(int i = len/2 - 1; i >= 0; i--){
            heapify(arr, i, len);
        }
        //从最后一个元素开始对序列进行调整,将最大值放到序列最后
        for(int i = len -1 ; i > 0 ; i--){
            int temp = arr[i];
            arr[i] = arr[0];
            arr[0] = temp;
            k--;
            if (k == 0){
                return arr[i];
            }
            //重新构建最大堆
            heapifyK(arr, 0, i);
        }
        return arr[0];
    }
    private static void heapifyK(int[] arr,int startIndex , int endIndex) {
        int max =  startIndex;
        //左节点
        int l = startIndex * 2 + 1;
        //右节点
        int r = l + 1;
        //获取父节点，左节点，右节点的最大值
        if(l < endIndex && arr[l] > arr[max]){
            max = l;
        }
        if(r < endIndex && arr[r] > arr[max]){
            max = r;
        }
        //交换最大值，并且重新构建子树的最大堆
        if (max != startIndex) {
            int temp = arr[startIndex];
            arr[startIndex] = arr[max];
            arr[max] = temp;
            heapifyK(arr, max, endIndex);
        }
    }
}

