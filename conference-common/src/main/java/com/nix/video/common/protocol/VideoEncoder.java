package com.nix.video.common.protocol;

import com.alipay.remoting.CommandEncoder;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.log.LogKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * @author keray
 * @date 2018/10/19 4:14 PM
 */
public class VideoEncoder implements CommandEncoder {
    /**
     * Encode object into bytes.
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof AbstractMessage) {
                AbstractMessage cmd = (AbstractMessage) msg;
                //写入协议byte
                out.writeByte(VideoProtocol.PROTOCOL_CODE);
                //写入message id
                out.writeInt(cmd.getId());
                //写入command类型
                out.writeShort(cmd.getCmdCode().value());
                //写入roomId(16byte)
                byte[] roomIdBytes = new byte[16];
                System.arraycopy(roomIdBytes,0,cmd.getRoomId().getBytes(),0,cmd.getRoomId().getBytes().length);
                out.writeBytes(roomIdBytes);
                //写入userId(16byte)
                byte[] userIdBytes = new byte[16];
                System.arraycopy(userIdBytes,0,cmd.getUserId().getBytes(),0,cmd.getUserId().getBytes().length);
                out.writeBytes(userIdBytes);
                //写入content长度
                out.writeInt(cmd.getContent().length);
                //写入content
                out.writeInt(cmd.getContent().length);
            } else {
                String warnMsg = "msg type [" + msg.getClass() + "] is not subclass of RpcCommand";
                LogKit.warn(warnMsg);
            }
        } catch (Exception e) {
            LogKit.error("Exception caught!", e);
            throw e;
        }
    }
}
