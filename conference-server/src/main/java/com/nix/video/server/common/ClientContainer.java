package com.nix.video.server.common;

import com.alipay.remoting.RemotingContext;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.log.LogKit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 11723
 * 客户端通道容器
 */
public class ClientContainer {
    /**
     * 房间号-房间的所有客户端的hello包信息
     * */
    private final static ConcurrentHashMap<String,List<RemotingContext>> CLIENT_CONTEXT = new ConcurrentHashMap<>();

    /**
     * 添加一个客户端连接
     * */
    public static void addClient(RemotingContext message, String roomId) {
        if (!CLIENT_CONTEXT.containsKey(roomId)) {
            if (!CLIENT_CONTEXT.containsKey(roomId)) {
                List<RemotingContext> list = Collections.synchronizedList(new ArrayList<>());
                CLIENT_CONTEXT.put(roomId,list);
                LogKit.info("新添加房间：" + roomId);
            }
        }
        LogKit.info("添加客户端" + message + "，roomId=" + roomId);
        CLIENT_CONTEXT.get(roomId).add(message);
    }
    /**
     * 根据{@link RemotingContext}客户端通道移除一个客户端
     * */
    public static void removeClient(RemotingContext ctx, AbstractMessage message) {
        LogKit.info("房间" + message.getRoomId() + "移除用户" + message.getUserId());
        CLIENT_CONTEXT.get(message.getRoomId()).remove(message);
    }
}
