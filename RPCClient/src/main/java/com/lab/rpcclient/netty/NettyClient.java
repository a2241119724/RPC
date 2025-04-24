package com.lab.rpcclient.netty;

import com.lab.rpcclient.netty.handler.ClientHeartBeatHandler;
import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.handler.HeartResponseHandler;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lab
 * @Title: NettyClient
 * @ProjectName RPC
 * @Description: 配置Netty连接
 * @date 2025/4/9 17:19
 */
public class NettyClient {
    @Resource
    private RPCEncoder rpcEncoder;
    @Resource
    private RPCDecoder rpcDecoder;
    @Resource
    private ClientHeartBeatHandler clientHeartBeatHandler;
    @Resource
    private HeartResponseHandler heartResponseHandler;

    private NioEventLoopGroup worker;
    private volatile Map<String, NettyClientHandler> handlers;

    public NettyClient(){
        handlers = new ConcurrentHashMap<>();
        worker = new NioEventLoopGroup();
    }

    public NettyClientHandler getConnection(InetSocketAddress serverAddress){
        return handlers.computeIfAbsent(serverAddress.toString(), k->createConnection(serverAddress));
    }

    public void removeConnect(InetSocketAddress serverAddress){
        handlers.remove(serverAddress.toString());
    }

    public NettyClientHandler createConnection(InetSocketAddress serverAddress){
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 长连接配置
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true)  // 启用 TCP keepalive
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
            }).connect(serverAddress.getAddress().getHostAddress(), serverAddress.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return nettyClientHandler;
    }
}
