package com.example.designpatterns.proxypatten.aopdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Main {
    public static ApplicationContext context;
    public static void main(String[] args) {
        context = new SpringApplication(Main.class).run(args);
        System.out.println("Main启动成功");
    }

}
