package com.meng.specialSubject.tree.bstree;

import lombok.Data;

public class BSTree {
    //根节点
    private Node root;
    public BSTree(){
        root = null;
    }
    //插入
    public boolean insert(int val){
        if (root == null){
            root = new Node(val);
            return true;
        }
        Node parent = null;
        Node current = root;
        while (current != null){
            if (val == current.val){
                return false;
            }else if (val < current.val){
                parent = current;
                current = current.left;
            }else {
                parent = current;
                current = current.right;
            }
        }
        if (val < parent.val){
            parent.left = new Node(val);
        } else {
            parent.right = new Node(val);
        }
         return true;
    }
    //查找
    public Node search(int val){
        Node current = root;
        while (current != null){
            if (val == current.val){
                return current;
            }else if (val < current.val){
                current = current.left;
            }else {
                current = current.right;
            }
        }
        return null;
    }
    //删除
    public void delete(int val){
        //寻找待删除的节点
        Node parent = null;
        Node current = root;
        while (current != null){
            if (val == current.val){
                break;
            }else if (val < current.val){
                parent = current;
                current = current.left;
            }else {
                parent = current;
                current = current.right;
            }
        }
        if (current == null){
            return;
        }
        //情况1：删除的节点没有左节点
        if (current.left == null ){

        }else if (current.right == null){

        }else {

        }
    }
    //中序遍历
    public void infixOrder(Node root){
        if (root == null){
            return;
        }
        if (root.getLeft() != null){
            infixOrder(root.getLeft());
        }
        System.out.print(root.getVal()+"-->");
        if (root.getRight() != null){
            infixOrder(root.getRight());
        }
    }
    //前序遍历
    public void preOrder(Node root) {
        if (root == null) {
            return;
        }
        System.out.print(root.getVal() + "-->");
        if (root.getLeft() != null) {
            preOrder(root.getLeft());
        }
        if (root.getRight() != null) {
            preOrder(root.getRight());
        }
    }
    //后序遍历
    public void postOrder(Node root){
        if (root == null) {
            return;
        }
        if (root.getLeft() != null) {
            postOrder(root.getLeft());
        }
        if (root.getRight() != null) {
            postOrder(root.getRight());
        }
        System.out.print(root.getVal() + "-->");
    }
    //中序遍历-非递归
    public void infixOrder2(Node root){

    }
    //前序遍历-非递归
    public void preOrder2(Node root){

    }
    //后序遍历-非递归
    public void postOrder2(Node root){

    }

    public static void main(String[] args) {
        BSTree bsTree = new BSTree();
        bsTree.insert(8);
        bsTree.insert(3);
        bsTree.insert(10);
        bsTree.insert(1);
        bsTree.insert(6);
        bsTree.insert(14);
        bsTree.insert(4);
        bsTree.insert(7);
        bsTree.insert(13);
        System.out.println("前序遍历");
        bsTree.preOrder(bsTree.root);
        System.out.println();
        System.out.println("中序遍历");
        bsTree.infixOrder(bsTree.root);
        System.out.println();
        System.out.println("后序遍历");
        bsTree.postOrder(bsTree.root);

    }
}
@Data
class Node{
    public int val;
    public Node left;
    public Node right;
    //构造器
    public Node(int val){
        this.val = val;
    }
}