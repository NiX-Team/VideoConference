package com.nix.server.common;

import com.nix.share.message.ImageMessage;
import io.netty.channel.ChannelHandlerContext;
import com.nix.share.util.log.LogKit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 11723
 * 客户端通道容器
 */
public class ClientContainer {
    /**
     * 房间号-房间的所有客户端的hello包信息
     * */
    private final static ConcurrentHashMap<String,List<ImageMessage>> CLIENT_CONTEXT = new ConcurrentHashMap<>();
    /**
     * 客户端通道-客户端的hello包
     * */
    private final static ConcurrentHashMap<ChannelHandlerContext,ImageMessage> USER_CONTEXT = new ConcurrentHashMap<>();
    private final static Object clock = new Object();
    /**
     * 添加一个客户端连接
     * */
    public static void addClient(ImageMessage message, String roomId) {
        if (!CLIENT_CONTEXT.containsKey(roomId)) {
            synchronized (clock) {
                if (!CLIENT_CONTEXT.containsKey(roomId)) {
                    List<ImageMessage> list = Collections.synchronizedList(new ArrayList<>());
                    CLIENT_CONTEXT.put(roomId,list);
                    LogKit.info("新添加房间：" + roomId);
                }
            }
        }
        LogKit.info("添加客户端" + message + "，roomId=" + roomId);
        USER_CONTEXT.put(message.getContext(),message);
        CLIENT_CONTEXT.get(roomId).add(message);
    }
    /**
     * 根据{@link ChannelHandlerContext}客户端通道移除一个客户端
     * */
    public static void removeClient(ChannelHandlerContext ctx) {
        ImageMessage message = USER_CONTEXT.get(ctx);
        LogKit.info("房间" + message.getRoomId() + "移除用户" + message.getUserId());
        CLIENT_CONTEXT.get(message.getRoomId()).remove(message);
        USER_CONTEXT.remove(message.getContext());
    }

    /**
     * 获取一个房间的所有客户端()
     * */
    public static List<ImageMessage> getRoomClients(String roomId) {
        //返回容器里的副本 不允许外界直接操作容器
        List list = CLIENT_CONTEXT.get(roomId);
        if (list == null) {
            return new ArrayList<>();
        }
        return new CopyOnWriteArrayList<>(CLIENT_CONTEXT.get(roomId));
    }

}
