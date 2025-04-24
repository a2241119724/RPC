package com.lab.rpcserver.netty;

import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.handler.HeartResponseHandler;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import com.lab.rpcserver.netty.handler.NettyServerHandler;
import com.lab.rpcserver.netty.handler.ServerHeartBeatHandler;
import com.lab.rpcserver.property.NettyServerProperty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @Title: NettyServer
 * @ProjectName RPC
 * @Description: 配置Netty连接
 * @date 2025/4/9 16:09
 */
public class NettyServer {
    @Resource
    public NettyServerHandler nettyServerHandler;
    @Resource
    public NettyServerProperty property;
    @Resource
    private RPCEncoder rpcEncoder;
    @Resource
    private RPCDecoder rpcDecoder;
    @Resource
    private ServerHeartBeatHandler serverHeartBeatHandler;
    @Resource
    private HeartResponseHandler heartResponseHandler;

    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("Boss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(new DefaultThreadFactory("Worker"));
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 长连接配置
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)  // 启用 TCP keepalive
                .childOption(ChannelOption.TCP_NODELAY, true);  // 禁用 Nagle 算法
        // 建立连接完成则不阻塞
        ChannelFuture future = bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                // out
                ch.pipeline().addLast(rpcEncoder);
                // in
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ProtocolConstant.MAX_FRAME_LENGTH,
                    12,4));
                ch.pipeline().addLast(rpcDecoder);
                ch.pipeline().addLast(new IdleStateHandler(ProtocolConstant.HEART_TIME,0,0, TimeUnit.SECONDS));
                ch.pipeline().addLast(serverHeartBeatHandler);
                ch.pipeline().addLast(heartResponseHandler);
                ch.pipeline().addLast(nettyServerHandler);
                }
            }).bind(property.getHost(), property.getPort()).sync();
        // 关闭通道则不阻塞
        future.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
