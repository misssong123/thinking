package netty.nio.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 直接读取和修改文件
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {
        //获取文件位置
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");
        //获取通道
        FileChannel channel = randomAccessFile.getChannel();
        //获取buffer
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        //修改文件
        System.out.println(buffer.get(2));
        buffer.put(2,(byte)'A');
        System.out.println(buffer.get(2));
        //buffer.put(5,(byte)'a');//java.lang.IndexOutOfBoundsException
        randomAccessFile.close();
        System.out.println("修改成功~~");
    }
}
