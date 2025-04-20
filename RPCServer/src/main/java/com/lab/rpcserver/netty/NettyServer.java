package com.lab.rpcserver.netty;

import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCRequest;
import com.lab.rpcserver.netty.handler.NettyServerHandler;
import com.lab.rpcserver.property.NettyServerProperty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: NettyServer
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 16:09
 */
public class NettyServer {
    @Resource
    public NettyServerHandler nettyServerHandler;
    @Resource
    public NettyServerProperty property;

    private static final int MAX_FRAME_LENGTH = 1024;

    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("Boss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(new DefaultThreadFactory("Worker"));
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 建立连接完成则不阻塞
        ChannelFuture future = bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // out
                        ch.pipeline().addLast(new RPCEncoder());
                        // in
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,
                                13,4));
                        ch.pipeline().addLast(new RPCDecoder());
                        ch.pipeline().addLast(nettyServerHandler);
                    }
                }).bind(property.getHost(), property.getPort()).sync();
        // 关闭通道则不阻塞
        future.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
