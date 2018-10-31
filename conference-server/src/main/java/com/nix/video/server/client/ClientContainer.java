package com.nix.video.server.client;

import com.alipay.remoting.Connection;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.VideoAddressParser;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.util.HttpClient;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.common.WebConfig;
import com.nix.video.server.remoting.VideoRemotingServer;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * 客户端通道容器
 */
public class ClientContainer {
    /**
     * 房间号-房间的所有客户端的hello包信息
     * */
    private final static ConcurrentHashMap<String/*roomId*/,Set<String>/*url.getUniqueKey()*/> CLIENT_CONTEXT = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<String/*url.getUniqueKey()*/,String[]/*roomId*/> CHANNEL_ROOM = new ConcurrentHashMap<>();

    /**
     * 添加一个客户端连接
     * */
    public static boolean addClient(Connection connection, AbstractMessage message) {
        String key = connection.getUrl().getUniqueKey();
        if (!CLIENT_CONTEXT.containsKey(message.getRoomId())) {
            Set<String> list = Collections.synchronizedSet(new LinkedHashSet<>());
            if (CLIENT_CONTEXT.putIfAbsent( message.getRoomId(),list) == null) {
                LogKit.info("新添加房间：" + message.getRoomId());
            }
        }
        if (CLIENT_CONTEXT.get(message.getRoomId()).add(key)) {
            LogKit.info("添加客户端 msg={} key={}",message,key);
            CHANNEL_ROOM.put(key, new String[]{message.getRoomId(), message.getUserId()});
            return true;
        }
        return false;
    }
    /**
     * 根据{@link RemotingContext}客户端通道移除一个客户端
     * */
    public static boolean removeClient(Connection connection, AbstractMessage message) {
        LogKit.info("开始移除用户 {}",message);
        if (Boolean.valueOf(HttpClient.doHttp(WebConfig.WEB_HOST + message.getWebPath(), HttpClient.HttpMethod.DELETE,null))) {
            CLIENT_CONTEXT.get(message.getRoomId()).remove(connection.getUrl().getUniqueKey());
            message.setCommandCode(MessageCommandCode.SERVER_SAY_LEAVE);
            pushMessage2Room(message,connection);
            return true;
        } else {
            LogKit.warn("移除用户失败 userMsg={}",message);
            return false;
        }
    }

    /**
     * 服务器通知房间里的客户端 哪个连接断开了
     * */
    public static void removeClient(Connection connection) {
        String[] userMsg = CHANNEL_ROOM.get(connection.getUrl().getUniqueKey());
        LogKit.debug("客户端移除断开，开始关闭通道 {}-{}",userMsg[0],userMsg[1]);
        if (removeClient(connection,AbstractMessage.createServerSayLeaveMessage(userMsg[0],userMsg[1]))) {
            CHANNEL_ROOM.remove(connection.getUrl().getUniqueKey());
        }
    }

    public static void pushData2Room(AbstractMessage message,Connection channel) {
        message.setCommandCode(MessageCommandCode.SERVER_PUSH_DATA);
        pushMessage2Room(message,channel);
    }

    public static void pushMessage2Room(AbstractMessage message,Connection connection) {
        String ownerKey = connection.getUrl().getUniqueKey();
        CLIENT_CONTEXT.get(message.getRoomId()).stream().filter(key -> !key.equals(ownerKey)).forEach(client -> {
            try {
                VideoRemotingServer.server.getConnectionManager().get(client).getChannel().writeAndFlush(message);
            }catch (Exception e) {
                LogKit.error("server push data error");
            }
        });
    }
}
