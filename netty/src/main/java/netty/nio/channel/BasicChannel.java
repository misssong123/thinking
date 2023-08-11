package main.java.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BasicChannel {
    public static void main(String[] args) throws Exception{
       trans();
    }
    public static void write() throws Exception{
        //
        FileOutputStream fos = new FileOutputStream("2.txt");
        //获取通道
        FileChannel channel = fos.getChannel();
        //获取buffer
        ByteBuffer buffer = ByteBuffer.wrap("hello world!".getBytes());//默认进行flip
        //写入文件
        channel.write(buffer);
        //关闭文件
        fos.close();
    }
    public static void read() throws Exception{
        //
        File file = new File("2.txt");
        FileInputStream fos = new FileInputStream(file);
        //获取通道
        FileChannel channel = fos.getChannel();
        //获取buffer
        ByteBuffer buffer = ByteBuffer.allocate(20);
        //写入文件
        channel.read(buffer);
        //
        System.out.println(new String(buffer.array(),0,buffer.position()));
        //关闭文件
        fos.close();
    }

    /**
     * 复制文件
     */
    public static void copyFile() throws Exception{
        FileInputStream fis = new FileInputStream("2.txt");
        FileOutputStream fos = new FileOutputStream("3.txt");

        //获取对应通道
        FileChannel readChannel = fis.getChannel();
        FileChannel writeChannel = fos.getChannel();
        //构建buffer
        ByteBuffer buffer = ByteBuffer.allocate(10);
        while (true){
            buffer.clear();//清理历史遗漏状态

            //读取数据
            int read = readChannel.read(buffer);
            if (read == -1){//不存在数据
                break;
            }
            //数据反转
            buffer.flip();
            //数据写入
            writeChannel.write(buffer);
        }
        fis.close();
        fos.close();
    }

    public static void trans() throws Exception{
        //创建相关流
        FileInputStream fileInputStream = new FileInputStream("3.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("4.txt");

        //获取各个流对应的filechannel
        FileChannel sourceCh = fileInputStream.getChannel();
        FileChannel destCh = fileOutputStream.getChannel();

        //使用transferForm完成拷贝
        destCh.transferFrom(sourceCh,0,sourceCh.size());
        //关闭相关通道和流
        sourceCh.close();
        destCh.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
