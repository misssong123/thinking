package thinking.queuedemo.unsafedemo.priorityqueuedemo;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 基于二叉堆（通常是最小堆）实现
 */
public class PriorityQueueDemo {
    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(Integer::compareTo);
        for(int i = 0 ; i < 20 ; i++){
            queue.add((int)(Math.random()*1000));
        }
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }
        System.out.println(queue.poll());
    }

}
class MinHeap{
    private final List<Integer> heap;
    public MinHeap(){
        heap = new ArrayList<>();
    }
    //左子节点
    private int leftChild(int i){
        return 2*i+1;
    }
    //右子节点
    private int rightChild(int i){
        return 2*i+2;
    }
    //父节点
    private int parent(int i){
        return (i-1)/2;
    }
    //插入节点
    public void insert(int x){
        heap.add(x);
        int index = heap.size()-1;
        //上浮节点
        while(index > 0 && heap.get(index) < heap.get(parent(index))){
            swap(index, parent(index));
            index = parent(index);
        }
    }
    //交换节点
    private void swap(int i, int j) {
        int temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    //删除最小节点
    public int deleteMin(){
        if(heap.isEmpty()){
            throw new RuntimeException("堆为空");
        }
        if (heap.size() == 1){
            return heap.remove(0);
        }
        int min = heap.get(0);
        heap.set(0,heap.remove(heap.size()-1));
        minHeapify(0);
        return min;
    }
    //更新指定节点的最小树
    public void minHeapify(int i){
        //左子节点
        int left = leftChild(i);
        //右子节点
        int right = rightChild(i);
        //最小节点
        int smallest = i;
        //计算左节点
        if(left < heap.size() && heap.get(left) < heap.get(smallest)){
            smallest = left;
        }
        //计算右节点
        if(right < heap.size() && heap.get(right) < heap.get(smallest)){
            smallest = right;
        }
        if (smallest != i){
            swap(i, smallest);
            minHeapify(smallest);
        }
    }
}