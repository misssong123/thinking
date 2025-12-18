package com.example.designpatterns.mementopattern.improve;

import java.util.*;

public class HistoryManager {
    private final Deque<EnhancedMemento> undoStack = new ArrayDeque<>();
    private final Deque<EnhancedMemento> redoStack = new ArrayDeque<>();
    private final StateManager stateManager;
    private final int maxHistorySize;

    // 当前状态快照（用于支持多个分支）
    private EnhancedMemento currentSnapshot;

    public HistoryManager(StateManager stateManager) {
        this(stateManager, 50);
    }

    public HistoryManager(StateManager stateManager, int maxHistorySize) {
        this.stateManager = stateManager;
        this.maxHistorySize = maxHistorySize;
    }
    /**
     * 保存当前状态到历史记录
     */
    public void saveState(Memorable originator, String description) {
        try {
            EnhancedMemento memento = (EnhancedMemento) originator.saveToMemento(description);

            // 如果当前有快照，将其推入撤销栈
            if (currentSnapshot != null) {
                undoStack.push(currentSnapshot);
                manageStackSize();
            }

            currentSnapshot = memento;
            redoStack.clear(); // 新操作后清空重做栈

            // 可选：保存到持久化存储
            stateManager.saveToFile(memento);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save state", e);
        }
    }
    /**
     * 撤销操作
     */
    public boolean undo(Memorable originator) {
        if (undoStack.isEmpty() && currentSnapshot == null) {
            return false;
        }

        // 如果有当前快照，先保存到重做栈
        if (currentSnapshot != null) {
            redoStack.push(currentSnapshot);
        }

        // 从撤销栈恢复
        if (!undoStack.isEmpty()) {
            currentSnapshot = undoStack.pop();
            originator.restoreFromMemento(currentSnapshot);
            return true;
        } else {
            currentSnapshot = null;
        }

        return false;
    }
    /**
     * 重做操作
     */
    public boolean redo(Memorable originator) {
        if (redoStack.isEmpty()) {
            return false;
        }

        // 保存当前状态到撤销栈
        if (currentSnapshot != null) {
            undoStack.push(currentSnapshot);
            manageStackSize();
        }

        currentSnapshot = redoStack.pop();
        originator.restoreFromMemento(currentSnapshot);
        return true;
    }
    /**
     * 创建分支快照
     */
    public String createBranch(String branchName) {
        if (currentSnapshot != null) {
            EnhancedMemento branchSnapshot = currentSnapshot.deepCopy();
            branchSnapshot.putMetadata("branch", branchName);
            return branchSnapshot.getId();
        }
        return null;
    }

    /**
     * 获取历史记录列表
     */
    public List<EnhancedMemento> getHistory() {
        List<EnhancedMemento> history = new ArrayList<>(undoStack);
        Collections.reverse(history); // 按时间顺序

        if (currentSnapshot != null) {
            history.add(currentSnapshot);
        }

        List<EnhancedMemento> redoHistory = new ArrayList<>(redoStack);
        history.addAll(redoHistory);

        return history;
    }
    /**
     * 跳转到特定历史点
     */
    public boolean jumpToHistoryPoint(Memorable originator, String mementoId) {
        // 首先在当前栈中查找
        for (EnhancedMemento memento : undoStack) {
            if (memento.getId().equals(mementoId)) {
                // 保存当前状态到重做栈
                if (currentSnapshot != null) {
                    redoStack.push(currentSnapshot);
                }

                // 清空undo栈中该点之后的所有状态
                while (!undoStack.isEmpty() && !undoStack.peek().getId().equals(mementoId)) {
                    undoStack.pop();
                }

                currentSnapshot = undoStack.pop();
                originator.restoreFromMemento(currentSnapshot);
                return true;
            }
        }
        // 在持久化存储中查找
        try {
            EnhancedMemento memento = stateManager.loadFromFile(mementoId);
            if (memento != null) {
                // 保存当前状态到重做栈
                if (currentSnapshot != null) {
                    redoStack.push(currentSnapshot);
                }

                currentSnapshot = memento;
                originator.restoreFromMemento(currentSnapshot);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Failed to load memento: " + e.getMessage());
        }

        return false;
    }
    /**
     * 清空历史记录
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        currentSnapshot = null;
    }

    /**
     * 检查是否有可撤销的操作
     */
    public boolean canUndo() {
        return !undoStack.isEmpty() || currentSnapshot != null;
    }

    /**
     * 检查是否有可重做的操作
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    private void manageStackSize() {
        while (undoStack.size() > maxHistorySize) {
            undoStack.removeLast();
        }
    }
}
