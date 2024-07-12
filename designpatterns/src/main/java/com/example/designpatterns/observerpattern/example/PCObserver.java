package com.example.designpatterns.observerpattern.example;

public class PCObserver implements Observer {
    private  String name;
    PCObserver(){
        super();
        name = "电脑端";
    }
    @Override
    public void process(Message message) {
        System.out.println("【"+name+"】收到消息："+message.getMessage());
    }
}
