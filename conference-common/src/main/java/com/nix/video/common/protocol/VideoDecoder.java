package com.nix.video.common.protocol;

import com.alipay.remoting.CommandDecoder;
import com.nix.video.common.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author keray
 * @date 2018/10/19 4:14 PM
 */
public class VideoDecoder implements CommandDecoder {
    /**
     * Decode bytes into object.
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readByte() == VideoProtocol.PROTOCOL_CODE) {
            short code = in.readShort();
            int id = in.readInt();
            AbstractMessage message;
            byte[] roomIdBytes = new byte[16];
            byte[] userIdBytes = new byte[16];
            in.readBytes(roomIdBytes);
            in.readBytes(userIdBytes);
            if (code == MessageCommandCode.CLIENT_HELLO.value() || code == MessageCommandCode.CLIENT_LEAVE.value() ||
                    code == MessageCommandCode.SERVER_HELLO.value() || code == MessageCommandCode.SERVER_SAY_LEAVE.value()) {
                message = new AbstractMessage(new String(roomIdBytes),new String(userIdBytes),id);
                message.setCommandCode(MessageCommandCode.valueOfCode(code));
                out.add(message);
            }
            if (code == MessageCommandCode.CLIENT_PUSH_DATA.value() || code == MessageCommandCode.SERVER_PUSH_DATA.value()) {
                message = new AbstractMessage(new String(roomIdBytes),new String(userIdBytes),id);
                message.setCommandCode(MessageCommandCode.valueOfCode(code));
                byte[] content = new byte[in.readInt()];
                in.readBytes(content);
                message.setContent(content);
                out.add(message);
            }
        }
    }
}
