package netty.nio.channel.zerocopy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewServer {
    public static void main(String[] args) throws Exception{
        //服务段
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8099));
        //接收连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        //接收字节数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        long total = 0;
        while (true){
            int read = socketChannel.read(byteBuffer);
            if (read == -1){
                break;
            }
            total += read;
            byteBuffer.rewind();
        }
        System.out.println("共接收字节数组:"+total);
    }
}
