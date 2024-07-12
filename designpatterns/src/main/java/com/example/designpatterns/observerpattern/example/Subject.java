package com.example.designpatterns.observerpattern.example;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    List<Observer> observers;
    Subject(){
        observers = new ArrayList<Observer>();
    }
    // 添加观察者
    public void addObserver(Observer observer){
        observers.add(observer);
    }
    // 删除观察者
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }
    // 通知观察者
    public abstract void notifyObservers(Message message);
}
