package com.example.designpatterns.observerpattern.utilexample;
import java.util.Observer;

public class Test {
    public static void main(String[] args) {
        WorkSubject subject = new WorkSubject();
        Observer pcObserver = new PCObserver();
        Observer mobileObserver = new MobileObserver();
        Message message = new Message("用户下线。。。。");
        //添加观察者
        subject.addObserver(pcObserver);
        subject.addObserver(mobileObserver);
        //通知观察者
        subject.onMessage(message);
    }
}
