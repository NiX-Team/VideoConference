package com.nix.video.common;

import com.alipay.remoting.CommandCode;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.ProtocolManager;
import com.alipay.remoting.RemotingProcessor;

/**
 * @author keray
 * @date 2018/10/19 2:28 PM
 */
public interface RemotingService {
    /**
     * 启动服务
     * @return
     */
    boolean start();

    /**
     * 关闭服务
     * @return
     */
    boolean stop();

    /**
     * 注册处理器
     * */
    void registerProcessor(byte protocolCode, CommandCode cmd, RemotingProcessor<?> processor) ;
}
