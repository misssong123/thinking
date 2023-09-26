package netty.nio.buffer;

import java.nio.IntBuffer;

// 1. Buffer的使用
/**
 * 四大属性
 * 1.capacity 容量
 * 2.limit 限制
 * 3.position 位置
 * 4.mark
 */
public class BasicBuffer {
    public static void main(String[] args) {
        //1.创建一个buffer
        IntBuffer buffer = IntBuffer.allocate(10);
        //2.存放数据
        for (int i = 0 ; i < 10 ; i++){
            buffer.put(i);
        }
        buffer.flip();//反转
        //3.读取数据
        while(buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }

}
