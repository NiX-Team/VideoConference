package com.nix.video.common.protocol;

import com.alipay.remoting.CommandDecoder;
import com.nix.video.common.message.*;
import com.nix.video.common.util.log.LogKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author keray
 * @date 2018/10/19 4:14 PM
 */
public class VideoDecoder implements CommandDecoder {
    /**
     * DecommandCode bytes into object.
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            short commandCode = in.readShort();
            int id = in.readInt();
            AbstractMessage message = null;
            byte[] roomIdBytes = new byte[16];
            byte[] userIdBytes = new byte[16];
            in.readBytes(roomIdBytes);
            in.readBytes(userIdBytes);
            if (commandCode == MessageCommandCode.CLIENT_HELLO.value() || commandCode == MessageCommandCode.CLIENT_LEAVE.value() ||
                    commandCode == MessageCommandCode.SERVER_HELLO.value() || commandCode == MessageCommandCode.SERVER_SAY_LEAVE.value() ||
                    commandCode == MessageCommandCode.HEART_SYN_COMMAND.value() || commandCode == MessageCommandCode.HEART_ACK_COMMAND.value()) {
                message = new VideoRequestMessage(new String(roomIdBytes).trim(),new String(userIdBytes).trim(),id);
                message.setCommandCode(MessageCommandCode.valueOfCode(commandCode));
            }
            else if (commandCode == MessageCommandCode.CLIENT_PUSH_DATA.value() || commandCode == MessageCommandCode.SERVER_PUSH_DATA.value()) {
                message = new VideoRequestMessage(new String(roomIdBytes).trim(),new String(userIdBytes).trim(),id);
                message.setCommandCode(MessageCommandCode.valueOfCode(commandCode));
                byte[] content = new byte[in.readInt()];
                in.readBytes(content);
                message.setContent(content);
            } else if (commandCode == MessageCommandCode.RESPONSE.value()) {
                message = new VideoResponseMessage(id,new String(roomIdBytes).trim(),new String(userIdBytes).trim());
                byte[] content = new byte[in.readInt()];
                in.readBytes(content);
                message.setContent(content);

                LogKit.debug("解码响应数据包 {}",message);
            }
            // 反序列化
            if (message != null) {
                message.deserialize();
            }
            out.add(message);
        }catch (Exception ignored) {
        }
    }
}
