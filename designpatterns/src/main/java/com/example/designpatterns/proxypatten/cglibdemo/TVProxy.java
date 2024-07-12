package com.example.designpatterns.proxypatten.cglibdemo;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TVProxy<T> implements MethodInterceptor {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public T getProxy(Class<T> tClass) {
        this.setName("【"+tClass.getSimpleName()+"-PROXY】");
        //1.工具类
        Enhancer enhancer = new Enhancer();
        //2.设置父类
        enhancer.setSuperclass(tClass);
        //3.设置回调函数
        enhancer.setCallback(this);
        //4.创建子类（代理对象）
        return (T)enhancer.create();
    }
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println(name+"before");
        Object invoke = proxy.invokeSuper(obj, args);
        System.out.println(name+"after");
        return invoke;
    }
}
