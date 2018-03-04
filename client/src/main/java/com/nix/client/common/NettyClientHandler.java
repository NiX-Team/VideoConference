package com.nix.client.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import com.alibaba.fastjson.JSON;
import io.netty.util.CharsetUtil;

/**
 * @author 11723
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static ChannelHandlerContext context;


    /**
     * context写入数据
     * */
    public static void writeContent(ByteBuf content) {
        context.writeAndFlush(content);
    }

    /**
     *此方法会在连接到服务器后被调用
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
//        writeContent(Unpooled.copiedBuffer("Netty rocks!25151515", CharsetUtil.UTF_8));
    }
    /**
     *此方法会在接收到服务器数据后调用
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("server return data :" + msg);
    }

    /**
     *捕捉到异常
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
