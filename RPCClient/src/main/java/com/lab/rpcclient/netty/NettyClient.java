package com.lab.rpcclient.netty;

import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpccommon.utils.Utils;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import javax.annotation.Resource;
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
    @Resource
    private RPCEncoder rpcEncoder;
    @Resource
    private RPCDecoder rpcDecoder;

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
        NettyClientHandler nettyClientHandler = Utils.getBean(NettyClientHandler.class);
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
                    ch.pipeline().addLast(rpcEncoder);
                    // in
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,
                            12,4));
                    ch.pipeline().addLast(rpcDecoder);
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
