package netty.netty.simple;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public class Test {
    public static void main(String[] args) {
        System.out.println(NettyRuntime.availableProcessors());
        SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2);
    }
}
