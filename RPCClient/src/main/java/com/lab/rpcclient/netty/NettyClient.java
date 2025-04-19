package com.lab.rpcclient.netty;

import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpcclient.utils.BeanUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.stereotype.Controller;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lab
 * @Title: NettyClient
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 17:19
 */
public class NettyClient {
    public static final String DELIMITER = "\n";

    private volatile Map<String, NettyClientHandler> handlers;
    private volatile Map<NettyClientHandler, NioEventLoopGroup> groups;
    private static final int MAX_FRAME_LENGTH = 1024;

    public NettyClient(){
        handlers = new HashMap<>();
        groups = new HashMap<>();
    }

    public NettyClientHandler getConnection(InetSocketAddress serverAddress){
        if(!handlers.containsKey(serverAddress.toString())){
            synchronized (this){
                if(!handlers.containsKey(serverAddress.toString())){
                    NettyClientHandler handler = createConnection(serverAddress);
                    handlers.put(serverAddress.toString(), handler);
                }
            }
        }
        return handlers.get(serverAddress.toString());
    }

    public void removeConnection(InetSocketAddress serverAddress){
        synchronized (this){
            if(handlers.containsKey(serverAddress.toString())){
                NettyClientHandler remove = handlers.remove(serverAddress.toString());
                groups.remove(remove).shutdownGracefully();
            }
        }
    }

    public NettyClientHandler createConnection(InetSocketAddress serviceAddress){
        NettyClientHandler nettyClientHandler = BeanUtils.getBean(NettyClientHandler.class);
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = null;
        try {
            future = bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // out
                            ch.pipeline().addLast(new StringEncoder());
                            // in
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH,
                                    Unpooled.copiedBuffer(DELIMITER.getBytes())));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(nettyClientHandler);
                        }
                    }).connect(serviceAddress.getAddress().getHostAddress(), serviceAddress.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        groups.put(nettyClientHandler, worker);
        return nettyClientHandler;
    }
}
