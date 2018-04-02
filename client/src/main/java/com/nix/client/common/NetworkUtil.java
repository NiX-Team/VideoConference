package com.nix.client.common;

public interface NetworkUtil {
    /**
     * 第一次连接服务器发送hello包
     * */
    boolean connectServer(String host,int port);

    /**
     * 客户端重新连接
     * */
     boolean againConnect();

     void close();
    void sendImageMessage(ImageMessage message);

    public static String getRoomId() {
        return roomId;
    }

    public static void setRoomId(String roomId) {
        TCPUtil.roomId = roomId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        TCPUtil.userId = userId;
    }
}
