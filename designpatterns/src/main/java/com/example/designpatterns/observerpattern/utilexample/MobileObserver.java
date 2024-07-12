package com.example.designpatterns.observerpattern.utilexample;

import java.util.Observable;
import java.util.Observer;

public class MobileObserver implements Observer {
    private  String name;
    MobileObserver(){
        super();
        name = "手机端";
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Message){
            System.out.println(name+" 收到消息："+((Message) arg).getMessage());
        }
    }
}
