package com.example.designpatterns.observerpattern.example;

public class MobileObserver implements Observer {
    private  String name;
    MobileObserver(){
        super();
        name = "手机端";
    }
    @Override
    public void process(Message message) {
        System.out.println("【"+name+"】收到消息："+message.getMessage());
    }
}
