package com.example.designpatterns.mementopattern.common;

import java.util.LinkedList;

public class HistoryManager implements Caretaker{
    private LinkedList<Memento> undoStack = new LinkedList<>();
    private LinkedList<Memento> redoStack = new LinkedList<>();
    private Originator originator;
    public HistoryManager(Originator originator) {
        this.originator = originator;
    }
    @Override
    public void undo() {
        if (undoStack.size()>1) {
            redoStack.push(undoStack.pop());
            originator.restore(undoStack.peek());
        }
    }

    @Override
    public void redo() {
        if (!redoStack.isEmpty()) {
            Memento memento = redoStack.pop();
            undoStack.push(memento);
            originator.restore(memento);
        }
    }

    @Override
    public void save() {
        undoStack.push(originator.createMemento());
        redoStack.clear();
    }
}
