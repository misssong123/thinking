package netty.nio.channel.chatgroup;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client {
    //属性
    private Selector selector;
    private SocketChannel socketChannel;
    private int port = 6667;
    private String host = "127.0.0.1";
    private String username;
    //实例化
    public Client(){
        try {
            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            //连接
            socketChannel.connect(new InetSocketAddress(host,port));
            socketChannel.configureBlocking(false);
            //注册
            socketChannel.register(selector, SelectionKey.OP_READ);
            //得到username
            username = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(username + " is ok...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //向服务器发送消息
    public void sendInfo(String info) {
        info = username + " 说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    //接收消息
    //向服务器发送消息
    public void readInfo() {
        try {
            int select = selector.select();
            if (select > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey next = iterator.next();
                    if (next.isReadable()){
                        SocketChannel channel = (SocketChannel)next.channel();
                        //得到一个Buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取
                        channel.read(buffer);
                        //把读到的缓冲区的数据转成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }
        }catch (Exception e){

        }
    }
    public static void main(String[] args) {
        //启动我们客户端
        Client chatClient = new Client();

        //启动一个线程, 每个3秒，读取从服务器发送数据
        new Thread(() -> {
            while (true) {
                chatClient.readInfo();
                try {
                    Thread.currentThread().sleep(3000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
