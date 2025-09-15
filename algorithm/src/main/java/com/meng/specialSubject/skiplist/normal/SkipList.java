package com.meng.specialSubject.skiplist.normal;

import lombok.Data;
import java.util.Random;
@Data
public class SkipList<T> {
    private SkipNode<T> head;
    private int level;
    private Random random;
    private static final int MAX_LEVEL = 32;
    private static final double PROMOTE_PROBABILITY = 0.5;
    public SkipList() {
        head = new SkipNode<T>(Integer.MIN_VALUE, null);
        level = 0;
        random = new Random();
    }
    //查询
    public SkipNode<T> search(int key) {
        SkipNode<T> temp = head;
        while (temp != null){
            if (temp.getNext() != null){
                if (temp.getNext().getKey() < key){
                    temp = temp.getNext();
                }else if (temp.getNext().getKey() == key){
                    return temp.getNext();
                }else {
                    temp = temp.getDown();
                }
            }else {
                temp = temp.getDown();
            }
        }
        return null;
    }
    //删除
    public void delete(int key) {
        SkipNode<T> temp = head;
        while(temp != null){
            if(temp.getNext() != null && temp.getNext().getKey() == key){
                temp.setNext(temp.getNext().getNext());
                temp = temp.getDown();
            }else if (temp.getNext() == null || temp.getNext().getKey() > key){
                temp = temp.getDown();
            }else {
                temp = temp.getNext();
            }
        }
        // 如果没有找到要删除的键，直接返回
        // 调整最高层级：如果最高层的头节点没有下一个节点，则降低层级并更新head
        while (level > 0 && head != null && head.getNext() == null) {
            head = head.getDown();
            level--;
        }
    }
    //插入
    public void insert(int key, T value) {
        // 首先检查键是否已存在
        if (search(key) != null) {
            throw new RuntimeException("Key already exists: " + key);
        }
        SkipNode<T>[] preNodes = new SkipNode[MAX_LEVEL+1];
        SkipNode<T> temp = head;
        //记录前置节点
        for(int i = level ; i >= 0 ; i--){
            // 向右移动到合适位置
            while (temp.getNext() != null && temp.getNext().getKey() < key){
                temp = temp.getNext();
            }
            preNodes[i] = temp;
            // 移动到下一层
            if (temp.getDown() != null) {
                temp = temp.getDown();
            }
        }
        //计算层高
        int newLevel = randomLevel();
        if(newLevel > level){
            SkipNode<T> prevHead = head; // 保存原头节点
            for (int i = level + 1; i <= newLevel; i++) {
                SkipNode<T> newHead = new SkipNode<>(Integer.MIN_VALUE, null);
                if (i == level + 1) {
                    newHead.setDown(prevHead); // 第一层新节点指向原头节点
                } else {
                    newHead.setDown(head); // 高层新节点指向直接下层的新头节点
                }
                preNodes[i] = newHead;
                head = newHead;
            }
            level = newLevel;
        }
        //插入节点
        SkipNode<T> downNode = null;
        for(int i = newLevel ; i >= 0 ; i--){
            SkipNode<T> newNode = new SkipNode<>(key,value);
            newNode.setNext(preNodes[i].getNext());
            preNodes[i].setNext(newNode);
            newNode.setDown(downNode);
            downNode = newNode;
        }
    }
    private int randomLevel() {
        int res = 0;
        while (res < MAX_LEVEL && random.nextDouble() < PROMOTE_PROBABILITY){
            res++;
        }
        return res;
    }
}
@Data
class SkipNode<T> {
    private int key;
    private T value;
    private SkipNode<T> next;
    private SkipNode<T> down;
    public SkipNode(int key, T value) {
        this.key = key;
        this.value = value;
        this.next = null;
        this.down = null;
    }
}