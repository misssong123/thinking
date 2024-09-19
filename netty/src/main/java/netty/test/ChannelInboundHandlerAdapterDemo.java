package netty.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChannelInboundHandlerAdapterDemo extends ChannelInboundHandlerAdapter {
    public ChannelInboundHandlerAdapterDemo() {
        super();
    }

    /**
     *注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * 取消注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * 连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     *在 Netty 中，当一个 Channel 变得不活跃时（例如，连接被断开），会调用此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     *它在接收到数据时会被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * 当Netty的事件循环(group)完成了对某个Channel的读取操作，并将读取到的数据缓冲区
     * 传递给下一个InboundHandler时，会调用channelReadComplete方法。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * 它用于处理那些不被Netty本身直接处理的自定义事件。
     * 当你想要在Channel的生命周期中插入自定义的逻辑，
     * 或者在某些特定时机触发某些操作时，就可以使用这个方法
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 用于通知Channel的可写状态发生了变化。这个事件由Netty的Channel处理器自动生成，
     * 当Channel因缓冲区满或其他原因变为不可写或重新变为可写时触发。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 表示在处理Channel事件时遇到了一个未被处理的异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
