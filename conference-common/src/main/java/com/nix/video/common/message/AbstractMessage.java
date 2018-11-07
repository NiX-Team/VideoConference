package com.nix.video.common.message;

import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.config.switches.ProtocolSwitch;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import com.nix.video.common.protocol.VideoProtocol;

/**
 * @author keray
 * @date 2018/11/06 下午7:25
 */
public abstract class AbstractMessage implements RemotingCommand {

    /**
     * id
     * */
    protected int id;

    /**
     * 消息类型
     * */
    protected CommandCode commandCode;

    /**
     * 房间号
     * */
    protected final String roomId;
    /**
     * 用户id
     * */
    protected final String userId;

    /**
     * 内容
     * */
    protected byte[] content = new byte[0];

    public AbstractMessage(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    @Override
    public ProtocolCode getProtocolCode() {
        return ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE);
    }

    @Override
    public byte getSerializer() {
        return 1;
    }

    @Override
    public ProtocolSwitch getProtocolSwitch() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CommandCode getCmdCode() {
        return commandCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCommandCode(CommandCode commandCode) {
        this.commandCode = commandCode;
    }


    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "id=" + id +
                ", commandCode=" + commandCode +
                ", roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
