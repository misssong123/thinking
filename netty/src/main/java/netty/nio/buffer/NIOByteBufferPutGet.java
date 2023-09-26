package netty.nio.buffer;

import java.nio.ByteBuffer;

/**
 * 按顺序读取
 */
public class NIOByteBufferPutGet {
    public static void main(String[] args) {
        //创建buffer
        ByteBuffer buffer = ByteBuffer.allocate(128);
        //put
        buffer.putInt(123);
        buffer.putChar('a');
        buffer.putLong(1024L);
        //反转
        buffer.flip();
        //get
        System.out.println(buffer.getInt());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getLong());
    }
}
