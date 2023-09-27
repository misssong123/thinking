package netty.nio.channel.zerocopy;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {
    public static void main(String[] args) throws Exception {
        //服务socket
        ServerSocket serverSocket = new ServerSocket(7001);
        while (true){
            Socket accept = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
            byte[] byteArray = new byte[4096];
            long count  = 0;
            while (true) {
                int readCount = dataInputStream.read(byteArray,0,byteArray.length);
                if (-1 == readCount) {
                    break;
                }
                count += readCount;
            }
            System.out.println("共读取字节数:"+count);
        }
    }
}
