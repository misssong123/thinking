package com.example.designpatterns.mementopattern.common;

/**
 * 管理者（Caretaker）
 */
public interface Caretaker {
    void undo();
    void redo();
    void save();
}
