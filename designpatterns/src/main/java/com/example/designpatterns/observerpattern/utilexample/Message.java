package com.example.designpatterns.observerpattern.utilexample;

public class Message {
    private  String message;
    Message(String message){
        this.message=message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

