package com.nix.video.common.message;
import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.config.switches.ProtocolSwitch;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import com.alipay.remoting.util.IDGenerator;
import com.nix.video.common.protocol.VideoProtocol;

/**
 * @author 11723
 */
public class VideoRequestMessage extends AbstractMessage {

    public VideoRequestMessage(String roomId, String userId) {
        super(roomId, userId);
        id = IDGenerator.nextId();
    }
    public VideoRequestMessage(String roomId, String userId, int id) {
        super(roomId, userId);
        this.id = id;
    }

    public static VideoRequestMessage createRequestMessage(String roomId, String userId, MessageCommandCode commandCode) {
        VideoRequestMessage message = new VideoRequestMessage(roomId,userId);
        message.setCommandCode(commandCode);
        return message;
    }

    public static VideoRequestMessage createClientSayHelloMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.CLIENT_HELLO);
    }
    public static VideoRequestMessage createClientLeaveMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.CLIENT_LEAVE);
    }
    public static VideoRequestMessage createClientPushDataMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.CLIENT_PUSH_DATA);
    }
    public static VideoRequestMessage createServerSayHelloMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.SERVER_HELLO);
    }
    public static VideoRequestMessage createServerSayLeaveMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.SERVER_SAY_LEAVE);
    }
    public static VideoRequestMessage createServerPushDataMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.SERVER_PUSH_DATA);
    }
    public static VideoRequestMessage createVideoDataMessage(String roomId, String userId) {
        return createRequestMessage(roomId,userId,MessageCommandCode.VIDEO_DATA);
    }
    public static VideoRequestMessage createHeardSynMessage() {
        return createRequestMessage("","",MessageCommandCode.HEART_SYN_COMMAND);
    }
    public static VideoRequestMessage createHeardAckMessage() {
        return createRequestMessage("","",MessageCommandCode.HEART_ACK_COMMAND);
    }

    /**
     * key
     * */
    public String getKey() {
        return getRoomId();
    }


    /**
     * 获取消息的唯一id
     * @return
     * */
    @Override
    public int getId() {
        return id;
    }


    @Override
    public InvokeContext getInvokeContext() {
        return null;
    }
    @Override
    public void serialize() throws SerializationException {

    }

    @Override
    public void deserialize() throws DeserializationException {

    }

    @Override
    public void serializeContent(InvokeContext invokeContext) throws SerializationException {

    }

    @Override
    public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {

    }


    public String getSign() {
        return getRoomId() + "-" + getUserId();
    }
    public String getWebPath() {
        return ":8080/server/" + getRoomId() + "/" + getUserId();
    }

    @Override
    public String toString() {
        return "VideoRequestMessage{" +
                "roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                ", id=" + id +
                ", commandCode=" + commandCode +
                '}';
    }
}
