package com.example.designpatterns.mementopattern.common;


import java.util.Date;

/**
 * 备忘录
 */
public class Memento {
    private String content;
    private Date timeStamp;
    public Memento(String content) {
        this.content = content;
        this.timeStamp = new Date();
    }
    public String getContent() {
        return content;
    }
    public Date getTimeStamp() {
        return timeStamp;
    }
}
