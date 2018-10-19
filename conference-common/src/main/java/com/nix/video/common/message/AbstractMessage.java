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
public class AbstractMessage implements RemotingCommand {
    /**
     * 房间号
     * */
    private final String roomId;
    /**
     * 用户id
     * */
    private final String userId;
    /**
     * 消息id
     * */
    private final int id;

    /**
     * 消息类型
     * */
    private CommandCode commandCode;

    /**
     * 响应类型
     * */

    /**
     * 消息内容
     * */
    private byte[] content;


    public AbstractMessage(String roomId,String userId) {
        id = IDGenerator.nextId();
        this.roomId = roomId;
        this.userId = userId;
    }
    public AbstractMessage(String roomId,String userId,int id) {
        this.roomId = roomId;
        this.userId = userId;
        this.id = id;
    }

    public static AbstractMessage createAbstractMessage(String roomId,String userId,MessageCommandCode commandCode) {
        AbstractMessage message = new AbstractMessage(roomId,userId);
        message.setCommandCode(commandCode);
        return message;
    }

    public static AbstractMessage createClientSayHelloMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.CLIENT_HELLO);
    }
    public static AbstractMessage createClientLeaveMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.CLIENT_LEAVE);
    }
    public static AbstractMessage createClientPushDataMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.CLIENT_PUSH_DATA);
    }
    public static AbstractMessage createServerSayHelloMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.SERVER_HELLO);
    }
    public static AbstractMessage createServerSayLeaveMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.SERVER_SAY_LEAVE);
    }
    public static AbstractMessage createServerPushDataMessage(String roomId,String userId) {
        return createAbstractMessage(roomId,userId,MessageCommandCode.SERVER_PUSH_DATA);
    }

    /**
     * 获取消息的唯一id
     * @return
     * */
    @Override
    public int getId() {
        return id;
    }
    /**
     * Get the code of the protocol that this command belongs to
     *
     * @return protocol code
     */
    @Override
    public ProtocolCode getProtocolCode() {
        return ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE);
    }


    /**
     * Get invoke context for this command
     *
     * @return context
     */
    @Override
    public InvokeContext getInvokeContext() {
        return null;
    }

    /**
     * Get serializer type for this command
     *
     * @return
     */
    @Override
    public byte getSerializer() {
        return 0;
    }

    /**
     * Get the protocol switch status for this command
     *
     * @return
     */
    @Override
    public ProtocolSwitch getProtocolSwitch() {
        return null;
    }

    /**
     * Serialize all parts of remoting command
     *
     * @throws SerializationException
     */
    @Override
    public void serialize() throws SerializationException {

    }

    /**
     * Deserialize all parts of remoting command
     *
     * @throws DeserializationException
     */
    @Override
    public void deserialize() throws DeserializationException {

    }

    /**
     * Serialize content of remoting command
     *
     * @param invokeContext
     * @throws SerializationException
     */
    @Override
    public void serializeContent(InvokeContext invokeContext) throws SerializationException {

    }

    /**
     * Deserialize content of remoting command
     *
     * @param invokeContext
     * @throws DeserializationException
     */
    @Override
    public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {

    }

    @Override
    public CommandCode getCmdCode() {
        return commandCode;
    }


    public String getUserId() {
        return userId;
    }
    public String getRoomId() {
        return roomId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }


    public void setCommandCode(CommandCode commandCode) {
        this.commandCode = commandCode;
    }
}
