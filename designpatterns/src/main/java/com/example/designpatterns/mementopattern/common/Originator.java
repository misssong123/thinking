package com.example.designpatterns.mementopattern.common;

/**
 * 原发器-保存备忘录状态
 */
public interface Originator {
    void printContent();
    Memento createMemento();
    void restore(Memento memento);
    void changeContent(String content);
}
