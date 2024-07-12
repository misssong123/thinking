package com.example.designpatterns.observerpattern.utilexample;


import java.util.Observable;
import java.util.Observer;

public class PCObserver implements Observer {
    private  String name;
    PCObserver(){
        super();
        name = "电脑端";
    }
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Message){
            System.out.println(name+" 收到消息："+((Message) arg).getMessage());
        }
    }
}
