package com.lab.rpccommon.factory;

import com.lab.rpccommon.pojo.RPCRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lab
 * @Title: SerializerFactory
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 16:13
 */
public class SerializerFactory {
   public static SerializerFactory Instance;

   private Map<String, JsonSerializer> map;

   private SerializerFactory(){
       map = new HashMap<>();
       map.put("json", new JsonSerializer());
   }

   public JsonSerializer getSerializer(String name){
       if(map.containsKey(name)){
           return map.get(name);
       }
       return null;
   }

   public static SerializerFactory getInstance(){
       if(Instance == null){
           synchronized (SerializerFactory.class){
               if(Instance == null){
                   Instance = new SerializerFactory();
               }
           }
       }
       return Instance;
   }
}
