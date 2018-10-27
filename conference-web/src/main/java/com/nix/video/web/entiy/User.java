package com.nix.video.web.entiy;

import java.io.Serializable;

/**
 * @author Kiss
 * @date 2018/10/27 11:51
 */
public class User implements Serializable {
    private String roomId;
    private String userId;

    public User(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public User() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSign() {
        return roomId + "-" + userId;
    }
}
