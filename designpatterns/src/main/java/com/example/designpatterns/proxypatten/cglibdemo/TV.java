package com.example.designpatterns.proxypatten.cglibdemo;

public class TV {
    // 电视名称
    private String name;
    // 电视产地
    private String address;
    public TV(String name, String address) {
        this.name = name;
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TV{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
