package com.lab.rpc.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lab
 * @title ProtocolDiffHandler
 * @projectName RPC
 * @description 对协议的检测，实现不同协议的不同处理
 * TODO
 * @date 2025/4/24 14:54
 */
public class ProtocolDetectionHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){

    }
}
