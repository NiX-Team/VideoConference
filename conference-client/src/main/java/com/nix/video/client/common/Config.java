package com.nix.video.client.common;

import com.alipay.remoting.Connection;

/**
 * @author 11723
 */
public class Config {
    private static String roomId;
    private static String userId;
    private static String serverHost;
    private static Integer serverPort;
    private static Connection connection;

    public static String getRoomId() {
        return roomId;
    }

    public static void setRoomId(String roomId) {
        Config.roomId = roomId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        Config.userId = userId;
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static void setServerHost(String serverHost) {
        Config.serverHost = serverHost;
    }

    public static Integer getServerPort() {
        return serverPort;
    }

    public static void setServerPort(Integer serverPort) {
        Config.serverPort = serverPort;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        Config.connection = connection;
    }

    public static String getServerUrl() {
        return serverHost == null ||  serverPort == null ? null : serverHost + ":" + serverPort;
    }
}
