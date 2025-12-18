package com.example.designpatterns.mementopattern.improve;

import java.io.Serializable;

/**
 * 可记忆对象接口，支持状态保存和恢复
 */
public interface Memorable extends Serializable {
    /**
     * 保存当前状态到备忘录
     */
    Memento saveToMemento(String description);

    /**
     * 从备忘录恢复状态
     */
    void restoreFromMemento(Memento memento);

    /**
     * 获取当前状态摘要（用于显示）
     */
    String getStateSummary();
}
