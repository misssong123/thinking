package com.meng.specialSubject.sort;

import java.util.ArrayList;
import java.util.List;

public class Sort08RadixSort {
    //基数排序-仅考虑正整数
    public void radixSort(int[] arr){
        //确定最大位数
        int digit = getDigit(arr);
        for(int i = 1 ; i <= digit ; i++){
            radixSort(arr,i);
        }
    }
    private int getDigit(int[] arr){
        int digit = 1;
        int base = 10;
        for(int i : arr){
            while (i >= base){
                base *= 10;
                digit++;
            }
        }
        return digit;
    }
    private void radixSort(int[] arr, int digit) {
        List<Integer>[] buckets = new List[10];
        for (int i = 0 ; i < buckets.length ; i++){
            buckets[i] = new ArrayList<>();
        }
        int base = (int)Math.pow(10,digit -1);
        for(int i : arr){
            int index = i / base % 10;
            buckets[index].add(i);
        }
        int index = 0;
        for (List<Integer> bucket : buckets){
            for(int num : bucket){
                arr[index++] = num;
            }
        }
    }
    //基数排序-考虑负数
    public void radixSortAll(int[] arr) {
        if (arr.length == 0) return;

        // 分离正数和负数
        List<Integer> positives = new ArrayList<>();
        List<Integer> negatives = new ArrayList<>();

        for (int num : arr) {
            if (num >= 0) {
                positives.add(num);
            } else {
                negatives.add(-num); // 将负数转为正数处理
            }
        }

        // 分别排序正数和负数
        if (!positives.isEmpty()) {
            radixSortPositive(positives);
        }
        if (!negatives.isEmpty()) {
            radixSortPositive(negatives);
            // 将排序后的负数反转并恢复负号
            for (int i = 0; i < negatives.size() / 2; i++) {
                int temp = negatives.get(i);
                negatives.set(i, negatives.get(negatives.size() - 1 - i));
                negatives.set(negatives.size() - 1 - i, temp);
            }
            for (int i = 0; i < negatives.size(); i++) {
                negatives.set(i, -negatives.get(i));
            }
        }

        // 合并结果：先放负数，再放正数
        int index = 0;
        for (int num : negatives) {
            arr[index++] = num;
        }
        for (int num : positives) {
            arr[index++] = num;
        }
    }

    // 仅处理正数的基数排序
    private void radixSortPositive(List<Integer> list) {
        if (list.isEmpty()) return;

        // 确定最大位数
        int digit = getDigit(list);

        for (int i = 1; i <= digit; i++) {
            radixSortPositive(list, i);
        }
    }

    private int getDigit(List<Integer> list) {
        int max = list.stream().max(Integer::compare).get();
        int digit = 0;
        while (max > 0) {
            digit++;
            max /= 10;
        }
        return digit == 0 ? 1 : digit; // 至少1位
    }

    private void radixSortPositive(List<Integer> list, int digit) {
        List<Integer>[] buckets = new ArrayList[10];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
        int base = (int) Math.pow(10, digit - 1);
        // 分配到桶中
        for (int num : list) {
            int index = (num / base) % 10;
            buckets[index].add(num);
        }
        // 收集回列表
        list.clear();
        for (List<Integer> bucket : buckets) {
            list.addAll(bucket);
        }
    }
}
