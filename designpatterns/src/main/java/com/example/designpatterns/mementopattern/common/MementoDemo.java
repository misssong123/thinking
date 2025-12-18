package com.example.designpatterns.mementopattern.common;

public class MementoDemo {
    public static void main(String[] args) {
        // 创建原始类
        Originator originator = new Editor("初始状态");
        // 创建历史记录管理类
        Caretaker caretaker  = new HistoryManager(originator);
        // 保存初始状态
        caretaker.save();
        // 打印初始状态
        originator.printContent();
        // 编辑内容
        originator.changeContent("编辑状态1");
        // 保存编辑后的状态
        caretaker.save();
        // 打印编辑后的状态
        originator.printContent();
        // 撤销编辑
        caretaker.undo();
        originator.printContent();
        // 重做编辑
        caretaker.redo();
        originator.printContent();
        originator.changeContent("编辑状态2");
        // 保存编辑后的状态
        caretaker.save();
        originator.printContent();
    }
}
