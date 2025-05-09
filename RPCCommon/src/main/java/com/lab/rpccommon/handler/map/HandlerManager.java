package com.lab.rpccommon.handler.map;

import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @Title: HandlerManager
 * @ProjectName RPC
 * @Description: 不同协议对应的不同的处理器链，实现对不同协议的解析
 * TODO
 * @date 2025/4/24 16:03
 */
public class HandlerManager {
    private static HandlerManager Instance;

    private Map<ProtocolType, List<ChannelHandler>> handlers;

    private HandlerManager(){
        handlers = new ConcurrentHashMap<>();
        List<ChannelHandler> handlers1 = new ArrayList<>();
        handlers.put(ProtocolType.Custom, handlers1);
    }

    public List<ChannelHandler> getHandlers(ProtocolType protocolType){
        return handlers.get(protocolType);
    }

    public enum ProtocolType{
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
