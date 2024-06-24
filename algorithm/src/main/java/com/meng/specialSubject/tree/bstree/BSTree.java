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
        if (current == root){//根节点
            //左右节点为空
            if (current.left == null && current.right == null){
                root = null;
            }//左节点为空
            else if (current.left == null){
                root = current.right;
            }//右节点为空
            else if (current.right == null){
                root = current.left;
            }//左右节点均不为空
            else {
                //找到右子树的最小节点
                Node minCurrent = current.right;
                Node minParent = current;
                while (minCurrent.left != null){
                    minParent = minCurrent;
                    minCurrent = minCurrent.left;
                }
                //替换节点
                current.val = minCurrent.val;
                //删除最小节点
                if (minParent.left == minCurrent){
                    minParent.left = minCurrent.right;
                }else {
                    minParent.right = minCurrent.right;
                }
            }
        }else {//非根节点
            //情况1：删除的节点没有左节点
            if (current.left == null ){
                if (parent.left == current){
                    parent.left = current.right;
                }else {
                    parent.right = current.right;
                }
            }//情况2：删除的节点没有右节点
            else if (current.right == null){
                if (parent.left == current){
                    parent.left = current.left;
                }else {
                    parent.right = current.left;
                }
            }//情况3：删除的节点有左节点和右节点
            else {
                //找到右子树的最小节点
                Node minCurrent = current.right;
                Node minParent = current;
                while (minCurrent.left != null){
                    minParent = minCurrent;
                    minCurrent = minCurrent.left;
                }
                //替换节点
                current.val = minCurrent.val;
                //删除最小节点
                if (minParent.left == minCurrent){
                    minParent.left = minCurrent.right;
                }else {
                    minParent.right = minCurrent.right;
                }
            }
        }

    }
    public void deleteProve(int val) {
        root = deleteNode(root, val);
    }

    private Node deleteNode(Node root, int val) {
        if (root == null) {
            return null;
        }
        // 递归寻找待删除的节点
        if (val < root.val) {
            root.left = deleteNode(root.left, val);
        } else if (val > root.val) {
            root.right = deleteNode(root.right, val);
        } else {
            // 找到要删除的节点
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            // 找到右子树的最小值节点
            root.val = findMin(root.right).val;
            // 删除右子树中的最小值节点
            root.right = deleteNode(root.right, root.val);
        }
        return root;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
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
        bsTree.insert(9);
        for(int i = 1 ; i < 15 ; i++){
            bsTree.deleteProve(i);
            System.out.println("前序遍历");
            bsTree.preOrder(bsTree.root);
            System.out.println();
            System.out.println("中序遍历");
            bsTree.infixOrder(bsTree.root);
            System.out.println();
            System.out.println("后序遍历");
            bsTree.postOrder(bsTree.root);
            System.out.println();
            System.out.println("====================================");
        }

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