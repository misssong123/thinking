package com.example.designpatterns.mementopattern.common;

/**
 * 原发器（Originator）- 需要保存状态的对象
 */

public class Editor implements Originator{
    private String content;
    public Editor(String content) {
        this.content = content;
    }
    @Override
    public void printContent() {
        System.out.println("当前内容：" + content);
    }

    @Override
    public Memento createMemento() {
        return new Memento(content);
    }
    public void changeContent(String content) {
        this.content = content;
    }
    @Override
    public void restore(Memento memento) {
        this.content = memento.getContent();
    }
}
