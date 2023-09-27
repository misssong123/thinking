package netty.nio.channel.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewClient {
    public static void main(String[] args) throws Exception{
        //本地文件
        File file = new File("/Users/mengsong/Downloads/书/码出高效-Java开发手册.pdf");
        FileInputStream fio = new FileInputStream(file);
        FileChannel readChannel = fio.getChannel();
        //客户端
        SocketChannel socketChannel = SocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8099);
        socketChannel.connect(inetSocketAddress);
        //准备发送
        long startTime = System.currentTimeMillis();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        long sum = 0;
        while (true){
            byteBuffer.clear();
            int count = readChannel.read(byteBuffer);
            if (count == -1){
                break;
            }
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            sum += count;
        }
        System.out.println("发送总字节数： " + sum + ", 耗时： " + (System.currentTimeMillis() - startTime));
        readChannel.close();
        fio.close();
        socketChannel.close();
    }
}
