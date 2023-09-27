package netty.nio.channel.zerocopy;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class OldClient {
    public static void main(String[] args) throws Exception {
        File file = new File("/Users/mengsong/Downloads/书/码出高效-Java开发手册.pdf");
        FileInputStream fio = new FileInputStream(file);
        Socket socket = new Socket("localhost", 7001);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[4096];
        int readCount;
        long total = 0;
        long startTime = System.currentTimeMillis();
        while ((readCount = fio.read(buffer)) >= 0) {
            total += readCount;
            if (readCount < 4096){
                System.out.println(readCount);
            }
            dataOutputStream.write(buffer,0,readCount);
            dataOutputStream.flush();
        }

        System.out.println("发送总字节数： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime));

        dataOutputStream.close();
        socket.close();
        fio.close();
    }
}
