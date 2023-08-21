package thinking.proxy.aopdemo.aop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/aop")
public class TVJoinPoint {
    @GetMapping(value = "/test")
    public void process(){
        System.out.println("process");
    }
}
