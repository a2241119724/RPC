package com.lab.rpcclient.netty;

import com.lab.rpcclient.annotation.RPCResource;
import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpcclient.utils.BeanUtils;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @Title: NettyClient
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 17:19
 */
public class NettyClient {
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

    public synchronized void removeConnection(InetSocketAddress serverAddress){
        if(handlers.containsKey(serverAddress.toString())){
            NettyClientHandler remove = handlers.remove(serverAddress.toString());
            groups.remove(remove).shutdownGracefully();
        }else{
            System.out.println("不包含需要删除的连接!!!");
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
                            ch.pipeline().addLast(new RPCEncoder());
                            // in
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,
                                    13,4));
                            ch.pipeline().addLast(new RPCDecoder());
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
