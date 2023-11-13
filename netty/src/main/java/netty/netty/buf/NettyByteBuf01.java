package netty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(10);
        System.out.println("capacity:"+buf.capacity()+";writerIndex:"
                +buf.writerIndex()+";readIndex:"+buf.readerIndex());//capacity:10;writerIndex:0;readIndex:0

        for(int i = 0 ; i < 11 ; i++){
            buf.writeByte(i);
        }
        System.out.println("capacity:"+buf.capacity()+";writerIndex:"
                +buf.writerIndex()+";readIndex:"+buf.readerIndex());//capacity:10;writerIndex:10;readIndex:0

        for(int i = 0 ; i < 10 ; i++){
            buf.getByte(i);
        }
        System.out.println("capacity:"+buf.capacity()+";writerIndex:"
                +buf.writerIndex()+";readIndex:"+buf.readerIndex());//capacity:10;writerIndex:10;readIndex:0

        for(int i = 0 ; i < 10 ; i++){
            System.out.println(buf.readByte());
        }
        System.out.println("capacity:"+buf.capacity()+";writerIndex:"
                +buf.writerIndex()+";readIndex:"+buf.readerIndex());//capacity:10;writerIndex:10;readIndex:10
    }
}
