package com.example.designpatterns.observerpattern.example;

public class Test {
    public static void main(String[] args) {
        Subject subject = new WorkSubject();
        Observer pcObserver = new PCObserver();
        Observer mobileObserver = new MobileObserver();
        Message message = new Message("用户下线。。。。");
        //添加观察者
        subject.addObserver(pcObserver);
        subject.addObserver(mobileObserver);
        //通知观察者
        subject.notifyObservers(message);
    }
}
