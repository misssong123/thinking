package netty.bio;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BIOServer {
    public static void main(String[] args) throws Exception{
        //生成线城池减少开销
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10,10,3, TimeUnit.SECONDS,new LinkedBlockingDeque<>(100));
        //创建server,监听888端口号
        ServerSocket serverSocket = new ServerSocket(888);
        System.out.println("【888】服务器启动。。。。。。");
        while (true){
            //获取链接的socket
            Socket socket = serverSocket.accept();
            //处理socket
            poolExecutor.execute(()->handler(socket));
        }
    }
    private static void handler(Socket socket) {
        try {
            //打印当前的线程信息
            System.out.println("当前线程【"+Thread.currentThread().getName()+"】开始连接。。。。。。");
            //初始化接收字节数组
            byte[] bytes = new byte[1024];
            //获取socket对应的输入流
            InputStream inputStream = socket.getInputStream();
            while (true){
                int count = inputStream.read(bytes);
                if (count==-1){
                    break;
                }
                System.out.println("【"+Thread.currentThread().getName()+"】:"+new String(bytes,0,count));
            }
        }catch (Exception e){

        }finally {
            System.out.println("当前线程【"+Thread.currentThread().getName()+"】断开连接。。。。。。");
            try {
                socket.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
