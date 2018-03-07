package com.nix.server.common;

import com.nix.message.ImageMessage;
import io.netty.channel.ChannelHandlerContext;
import util.log.LogKit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 11723
 * 客户端通道容器
 */
public class ClientContainer {
    private final static ConcurrentHashMap<String,List<ChannelHandlerContext>> CLIENT_CONTEXT = new ConcurrentHashMap<>();
    private final static Object clock = new Object();
    /**
     * 添加一个客户端连接
     * */
    public static void addClient(ChannelHandlerContext context, String roomId) {
        if (!CLIENT_CONTEXT.containsKey(roomId)) {
            synchronized (clock) {
                if (!CLIENT_CONTEXT.containsKey(roomId)) {
                    List<ChannelHandlerContext> list = Collections.synchronizedList(new ArrayList<>());
                    CLIENT_CONTEXT.put(roomId,list);
                    LogKit.info("新添加房间：" + roomId);
                }
            }
        }
        LogKit.info("添加客户端" + context + "，roomId=" + roomId);
        CLIENT_CONTEXT.get(roomId).add(context);
    }
    /**
     * 移除一个客户端
     * */
    public static void removeClient(String roomId,ChannelHandlerContext context) {
        LogKit.info("房间" + roomId + "移除客户端" + context.hashCode());
        CLIENT_CONTEXT.get(roomId).remove(context);
    }

    /**
     * 获取一个房间的所有客户端()
     * */
    public static List<ChannelHandlerContext> getRoomClients(String roomId) {
        //返回容器里的副本 不允许外界直接操作容器
        List list = CLIENT_CONTEXT.get(roomId);
        if (list == null) {
            return new ArrayList<>();
        }
        return new CopyOnWriteArrayList<>(CLIENT_CONTEXT.get(roomId));
    }

}