package com.example.designpatterns.observerpattern.example;

public class WorkSubject extends Subject {
    @Override
    public void notifyObservers(Message message) {
        // 工作状态发生改变，通知所有观察者
        for (Observer observer : observers) {
            observer.process(message);
        }
    }
}
