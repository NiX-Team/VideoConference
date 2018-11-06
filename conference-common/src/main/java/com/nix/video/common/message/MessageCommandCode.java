package com.nix.video.common.message;

import com.alipay.remoting.CommandCode;

import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/10/19 1:56 PM
 */
public enum MessageCommandCode implements CommandCode {
    /** 请求响应包*/
    RESPONSE((short) 0x00),
    /** 客户端第一次连接 */
    CLIENT_HELLO((short) 0x01),
    /** 客户端离开 */
    CLIENT_LEAVE((short) 0x02),
    /** 客户端发送数据 */
    CLIENT_PUSH_DATA((short) 0x03),
    /** 服务器通知有个客户端进房间 */
    SERVER_HELLO((short) 0X04),
    /** 服务器通知有个客户端离开房间 */
    SERVER_SAY_LEAVE((short) 0X05),
    /** 服务器发送数据 */
    SERVER_PUSH_DATA((short) 0x06),
    /** 请求心跳数据包 */
    HEART_SYN_COMMAND((short) 0x07),
    /** 确认心跳数据包 */
    HEART_ACK_COMMAND((short) 0x08),
    /** 视频数据包*/
    VIDEO_DATA((short) 0x09);
    /** 编码 */
    short code;
    /**
     * the short value of the code
     */
    MessageCommandCode(short code) {
        this.code = code;
    }

    /**
     * @return the short value of the code
     */
    @Override
    public short value() {
        return code;
    }
    public static MessageCommandCode valueOfCode(short code) {
        for (MessageCommandCode e:values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
