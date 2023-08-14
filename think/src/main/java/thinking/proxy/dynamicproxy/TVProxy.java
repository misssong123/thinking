package thinking.proxy.dynamicproxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TVProxy {
    private Object target;

    public TVProxy(Object target) {
        this.target = target;
    }
    public Object getProxy(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("---------------代理商开始操作----------------");
                Object invoke = method.invoke(target, args);
                System.out.println("---------------代理商结束操作----------------");
                return invoke;
            }
        });
    }
}
