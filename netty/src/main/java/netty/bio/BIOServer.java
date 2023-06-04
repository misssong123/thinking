package main.java.netty.bio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String[] args) throws Exception{
        //创建一个线程池
        //如果有客户端连接，就创建一个线程，与之通讯（单独写一个方法）
        ExecutorService executors = Executors.newCachedThreadPool();
        //创建ServerSocket
        ServerSocket server = new ServerSocket(8888);
        //处理连接客户端
        while (true){
            Socket socket = server.accept();
            executors.execute(()->{
                //可以和客户端通讯
                handler(socket);
            });
        }

    }
    private static void handler(Socket socket) {
        //输出当前线程的名字
        System.out.println("当前线程的名字："+Thread.currentThread().getName());
        byte[] bytes = new byte[1024] ;
        while (true){
            try {
                //读取数据（阻塞）
                System.out.println("准备read...");
                int read = socket.getInputStream().read(bytes);
                if (read != -1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
                break;
            }finally {
                System.out.println("关闭和client的连接");
                try {
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
