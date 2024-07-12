package com.example.designpatterns.observerpattern.utilexample;
import java.util.Observable;
public class WorkSubject extends Observable {
    public void onMessage(Message message){
        // 设置消息状态
        setChanged();
        // 通知所有观察者
        notifyObservers(message);
    }
}
