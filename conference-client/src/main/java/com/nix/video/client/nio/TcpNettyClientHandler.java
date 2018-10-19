package com.nix.video.client.nio;
import com.nix.video.client.Main;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.impl.ImageMessage;
import com.nix.video.common.util.log.LogKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.Serializable;

/**
 * @author 11723
 */
public class TcpNettyClientHandler<M extends Serializable> extends AbstractClientHandler<AbstractMessage> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                num.getAndIncrement();
                ctx.writeAndFlush(ImageMessage.getPingMessage());
                if (num.get() > COUNT) {
                    Main.main.mainController.setError("服务器掉线\n尝试重新连接");
                    LogKit.info("服务器掉线\n尝试重新连接");
                    if (networkUtil.againConnect()) {
                        Main.main.mainController.setError("重新连接成功");
                        LogKit.info("重新连接成功");
                    }else {
                        Main.main.mainController.setError("重新连接失败");
                        LogKit.info("重新连接失败");
                        networkUtil.close();
                    }
                }
            }
        }
    }

}
