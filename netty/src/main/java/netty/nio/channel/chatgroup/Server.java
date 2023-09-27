package netty.nio.channel.chatgroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Server {
    //属性
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int port = 6667;
    Server(){
        try{
            //实例化
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            //绑定端口号
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            //设置非阻塞
            serverSocketChannel.configureBlocking(false);
            //注册接收事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){

        }finally {

        }


    }
    //事件监听
    public void listen(){
        System.out.println("监听线程: " + Thread.currentThread().getName());
        try {
            while (true){
                int count = selector.select();
                if (count>0){
                    //获取注册的key
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //获取SelectionKey
                        SelectionKey next = iterator.next();
                        //连接事件
                        if (next.isAcceptable()){
                            //获取连接
                            SocketChannel accept = serverSocketChannel.accept();
                            //设置非阻塞
                            accept.configureBlocking(false);
                            //注册到selector
                            accept.register(selector,SelectionKey.OP_READ);
                            //提示
                            System.out.println(accept.getRemoteAddress() + " 上线 ");
                        }else if (next.isReadable()){
                            readInfo(next);
                        }
                        //移除本次SelectionKey
                        iterator.remove();
                    }
                }else {
                    //休眠500毫秒
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        }catch (Exception e){

        }

    }
    //读取消息
    private void readInfo(SelectionKey key){
        //取到关联的channle
        SocketChannel channel = null;
        try{
            channel = (SocketChannel)key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read = channel.read(byteBuffer);
            if (read > 0){
                String msg = new String(byteBuffer.array(),0,read);
                //输出该消息
                System.out.println("form 客户端: " + msg);
                //转发消息
                sendInfoToOtherClients(msg,channel);
            }
        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了..");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            }catch (IOException e2) {
                e2.printStackTrace();;
            }
        }
    }
    //通知其他人
    private void sendInfoToOtherClients(String msg, SocketChannel self ) throws  IOException{
        for(SelectionKey key : selector.keys()){
            //通过 key  取出对应的 SocketChannel
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != self){
                SocketChannel socketChannel = (SocketChannel)channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(byteBuffer);
            }
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
