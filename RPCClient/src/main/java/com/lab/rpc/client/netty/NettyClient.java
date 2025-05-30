package com.lab.rpc.client.netty;

import cn.hutool.log.Log;
import com.lab.rpc.client.discovery.IServerDiscovery;
import com.lab.rpc.client.netty.handler.NettyClientHandler;
import com.lab.rpc.client.netty.handler.ClientHeartBeatHandler;
import com.lab.rpc.common.constant.ProtocolConstant;
import com.lab.rpc.common.handler.HeartResponseHandler;
import com.lab.rpc.common.handler.RpcDecoder;
import com.lab.rpc.common.handler.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author lab
 * @title NettyClient
 * @projectName RPC
 * @description 配置Netty连接
 * @date 2025/4/9 17:19
 */
@Slf4j
public class NettyClient {
    @Resource
    private RpcEncoder rpcEncoder;
    @Resource
    private RpcDecoder rpcDecoder;
    @Resource
    private ClientHeartBeatHandler clientHeartBeatHandler;
    @Resource
    private HeartResponseHandler heartResponseHandler;
    @Resource
    private IServerDiscovery serverDiscovery;

    private final NioEventLoopGroup worker;
    /**
     * String 服务名:IP
     */
    private volatile Map<String, List<NettyClientHandler>> handlers;

    private static final int MAX_CONNECTIONS = 10;
    private final Random random = new Random();

    public NettyClient(){
        handlers = new ConcurrentHashMap<>();
        worker = new NioEventLoopGroup();
    }

    public void preCreateConnection(String serverName){
        for (InetSocketAddress inetSocketAddress : serverDiscovery.getAllInstance(serverName)) {
            List<NettyClientHandler> handlers0 = new ArrayList<>();
            String key = serverName+":"+inetSocketAddress;
            if(handlers.containsKey(key) && handlers.get(key).size() >= MAX_CONNECTIONS){
                continue;
            }
            for (int i = 0; i < MAX_CONNECTIONS; i++){
                handlers0.add(createConnection(inetSocketAddress));
            }
            handlers.put(key, handlers0);
        }
    }

    public NettyClientHandler getConnection(String serverName){
        InetSocketAddress instance = serverDiscovery.getInstance(serverName);
        if(instance == null){
            return null;
        }
        String key = serverName+":"+instance;
        if(!handlers.containsKey(key) || handlers.get(key).size() < MAX_CONNECTIONS){
            preCreateConnection(serverName);
        }
        return handlers.get(key).get(random.nextInt(MAX_CONNECTIONS) % handlers.get(key).size());
    }

    /**
     * 移除连接
     * @param serverAddress
     * @param handler
     */
    public void removeConnect(InetSocketAddress serverAddress, NettyClientHandler handler){
        for (String key : handlers.keySet()){
            if(key.split(":",2)[1].equals(serverAddress.toString())){
                Iterator<NettyClientHandler> iterator = handlers.get(key).iterator();
                while (iterator.hasNext()){
                    NettyClientHandler next = iterator.next();
                    if(next.equals(handler)){
                        iterator.remove();
                        return;
                    }
                }
            }
        }
    }

    public NettyClientHandler createConnection(InetSocketAddress serverAddress){
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 长连接配置
            // 启用 TCP keepalive
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        // out
                        ch.pipeline().addLast(rpcEncoder);
                        // in
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ProtocolConstant.MAX_FRAME_LENGTH,
                                12, 4));
                        ch.pipeline().addLast(rpcDecoder);
                        ch.pipeline().addLast(new IdleStateHandler(0,ProtocolConstant.HEART_TIME / 2,0,TimeUnit.SECONDS));
                        ch.pipeline().addLast(clientHeartBeatHandler);
                        ch.pipeline().addLast(heartResponseHandler);
                        ch.pipeline().addLast(nettyClientHandler);
                    }
            }).connect(serverAddress.getAddress().getHostAddress(), serverAddress.getPort()).sync()
                    .addListener((ChannelFutureListener) future -> {
                        log.info(future.channel().id() + "连接成功");
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return nettyClientHandler;
    }
}
