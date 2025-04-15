package com.lab.provider.netty;

import com.lab.provider.netty.handler.NettyServerHandler;
import com.lab.provider.property.NettyServerProperty;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: NettyServer
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 16:09
 */
@Controller
public class NettyServer {
    @Resource
    public NettyServerHandler nettyServerHandler;
    @Resource
    public NettyServerProperty property;

    public static final String DELIMITER = "\n";

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
                        ch.pipeline().addLast(new StringEncoder());
                        // in
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH,
                                Unpooled.copiedBuffer(DELIMITER.getBytes())));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(nettyServerHandler);
                    }
                }).bind(property.getHost(), property.getPort()).sync();
        // 关闭通道则不阻塞
        future.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
