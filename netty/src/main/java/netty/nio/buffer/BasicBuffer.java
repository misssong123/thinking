package main.java.netty.nio.buffer;

import java.nio.IntBuffer;

// 1. Buffer的使用
public class BasicBuffer {
    public static void main(String[] args) {
        //1.创建一个buffer
        IntBuffer buffer = IntBuffer.allocate(10);
        //2.存放数据
        for (int i = 0 ; i < 10 ; i++){
            buffer.put(i);
        }
        //3.读取数据
        buffer.flip();
        while(buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }
}
