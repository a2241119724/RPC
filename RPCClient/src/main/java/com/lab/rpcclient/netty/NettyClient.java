package com.lab.rpcclient.netty;

import com.lab.rpcclient.netty.handler.ClientHeartBeatHandler;
import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import com.lab.rpccommon.utils.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
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
 * @Description: TODO
 * @date 2025/4/9 17:19
 */
public class NettyClient {
    @Resource
    private RPCEncoder rpcEncoder;
    @Resource
    private RPCDecoder rpcDecoder;
    @Resource
    private ClientHeartBeatHandler clientHeartBeatHandler;

    private Bootstrap bootstrap;
    private volatile Map<String, Channel> channels;
    private NettyClientHandler nettyClientHandler;

    public NettyClient(){
        channels = new ConcurrentHashMap<>();
        nettyClientHandler = Utils.getBean(NettyClientHandler.class);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(worker)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                // out
                ch.pipeline().addLast(rpcEncoder);
                // in
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ProtocolConstant.MAX_FRAME_LENGTH,
                    12, 4));
                ch.pipeline().addLast(rpcDecoder);
                ch.pipeline().addLast(new IdleStateHandler(0,ProtocolConstant.HEART_TIME / 2,0,TimeUnit.SECONDS));
                ch.pipeline().addLast(clientHeartBeatHandler);
                ch.pipeline().addLast(nettyClientHandler);
                }
            });
    }

    public NettyClientHandler getConnection(InetSocketAddress serverAddress){
        channels.computeIfAbsent(serverAddress.toString(), k->createConnection(serverAddress));
        return nettyClientHandler;
    }

    public void removeConnect(InetSocketAddress serverAddress){
        channels.remove(serverAddress.toString());
    }

    public Channel createConnection(InetSocketAddress serverAddress){
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(serverAddress.getAddress().getHostAddress(), serverAddress.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return future.channel();
    }
}
