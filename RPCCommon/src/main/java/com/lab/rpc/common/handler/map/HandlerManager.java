package com.lab.rpc.common.handler.map;

import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @title HandlerManager
 * @projectName RPC
 * @description 不同协议对应的不同的处理器链，实现对不同协议的解析
 * TODO
 * @date 2025/4/24 16:03
 */
public class HandlerManager {
    private static volatile HandlerManager Instance;

    private final Map<ProtocolType, List<ChannelHandler>> handlers;

    private HandlerManager(){
        handlers = new ConcurrentHashMap<>();
        List<ChannelHandler> handlers1 = new ArrayList<>();
        handlers.put(ProtocolType.Custom, handlers1);
    }

    public List<ChannelHandler> getHandlers(ProtocolType protocolType){
        return handlers.get(protocolType);
    }

    public enum ProtocolType{
        // 自定义协议
        Custom,
    }

    public static HandlerManager getInstance(){
        if(Instance == null){
            synchronized (HandlerManager.class){
                if(Instance == null){
                    Instance = new HandlerManager();
                }
            }
        }
        return Instance;
    }
}
