package com.nix.video.common.message;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.CommandCode;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;

import java.io.Serializable;

/**
 * @author keray
 * @date 2018/11/06 下午7:12
 */
public class VideoResponseMessage extends AbstractMessage {


    private Serializable responseObject;


    public VideoResponseMessage(int id,String roomId, String userId) {
        super(roomId, userId);
        this.id = id;
    }


    @Override
    public CommandCode getCmdCode() {
        return MessageCommandCode.RESPONSE;
    }


    @Override
    public InvokeContext getInvokeContext() {
        return null;
    }


    @Override
    public void serialize() throws SerializationException {
        setContent(JSON.toJSONBytes(responseObject));
    }

    @Override
    public void deserialize() throws DeserializationException {
        responseObject = JSON.parseObject(new String(getContent()),Serializable.class);
    }

    @Override
    public void serializeContent(InvokeContext invokeContext) throws SerializationException {

    }

    @Override
    public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {

    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Serializable responseObject) {
        this.responseObject = responseObject;
    }
    public static VideoResponseMessage createResponse(AbstractMessage request,Serializable response) {
        VideoResponseMessage responseMessage = new VideoResponseMessage(request.getId(),request.getRoomId(),request.getUserId());
        responseMessage.setResponseObject(response);
        return responseMessage;
    }
}
