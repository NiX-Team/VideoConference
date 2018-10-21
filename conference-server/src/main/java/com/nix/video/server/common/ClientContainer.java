package com.nix.video.server.common;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.VideoAddressParser;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.socket.VideoRemotingServer;
import io.netty.channel.Channel;

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
    private final static ConcurrentHashMap<String/*roomId*/,List<String>/*url.getUniqueKey()*/> CLIENT_CONTEXT = new ConcurrentHashMap<>();
    private final static VideoAddressParser ADDRESS_PARSER = VideoAddressParser.PARSER;

    /**
     * 添加一个客户端连接
     * */
    public static void addClient(Channel channel, AbstractMessage message) {

        if (!CLIENT_CONTEXT.containsKey(message.getRoomId())) {
            List<String> list = Collections.synchronizedList(new ArrayList<>());
            if (CLIENT_CONTEXT.putIfAbsent( message.getRoomId(),list) == null) {
                LogKit.info("新添加房间：" + message.getRoomId());
            }
        }
        LogKit.info("添加客户端" + message + "，roomId=" + message.getRoomId());
        CLIENT_CONTEXT.get(message.getRoomId()).add(ADDRESS_PARSER.parse(RemotingUtil.parseRemoteAddress(channel)).getUniqueKey());

    }
    /**
     * 根据{@link RemotingContext}客户端通道移除一个客户端
     * */
    public static void removeClient(Channel channel, AbstractMessage message) {
        LogKit.info("房间" + message.getRoomId() + "移除用户" + message.getUserId());
        CLIENT_CONTEXT.get(message.getRoomId()).remove(ADDRESS_PARSER.parse(RemotingUtil.parseRemoteAddress(channel)).getUniqueKey());
        message.setCommandCode(MessageCommandCode.SERVER_SAY_LEAVE);
        pushMessage2Room(message);
    }

    public static void pushData2Room(AbstractMessage message) {
        message.setCommandCode(MessageCommandCode.SERVER_PUSH_DATA);
        pushMessage2Room(message);
    }
    private static void pushMessage2Room(AbstractMessage message) {
        CLIENT_CONTEXT.get(message.getRoomId()).forEach(client -> {
            try {
                VideoRemotingServer.server.getConnectionManager().get(client).getChannel().writeAndFlush(message);
            }catch (Exception e) {
                LogKit.error("server push data error");
            }
        });
    }
}
