package com.example.designpatterns.mementopattern.improve;

import java.io.Serializable;
import java.util.Date;
/**
 * 备忘录接口
 */
public interface Memento extends Serializable {
    String getId();
    Date getTimestamp();
    String getDescription();
}
