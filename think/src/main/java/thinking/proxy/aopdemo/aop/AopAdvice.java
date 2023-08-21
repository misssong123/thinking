package thinking.proxy.aopdemo.aop;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopAdvice {
    public void before() {
        System.out.println("before");
    }

    public void after() {
        System.out.println("after");
    }
}
