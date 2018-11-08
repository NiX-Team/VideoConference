package com.nix.video.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;


/**
 * @author keray
 * @date 2018/10/27 14:11
 */
public class HttpConnect {
    public final static Charset UTF_8 = Charset.forName("UTF-8");
    public enum HttpMethod{
        POST,
        PUT,
        DELETE,
        GET
    }
    public static String doHttp(String url,HttpConnect.HttpMethod method ,  Map<String,String> header,Object bodyParam){
        try {
            return new HttpConnect.Builder()
                    .url(url)
                    .method(method)
                    .header(header)
                    .param(bodyParam)
                    .build().sendData().asString(UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String doHttp(String url,HttpConnect.HttpMethod method ,Object param){
        try {
            return new HttpConnect.Builder()
                    .url(url)
                    .method(method)
                    .param(param)
                    .build().sendData().asString(UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object param;
    private String url;
    private HttpMethod method;
    private Map<String, String> header;


    public static class Builder {
        private Object param;
        private String url;
        private HttpMethod method;
        private Map<String, String> header;


        public Builder param(Object val) {
            param = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder method(HttpMethod val) {
            method = val;
            return this;
        }

        public Builder header(Map<String, String> val) {
            header = val;
            return this;
        }

        public HttpConnect build() {
            return new HttpConnect(this);
        }


    }

    private HttpConnect(Builder builder) {
        param = builder.param;
        url = builder.url;
        method  = builder.method;
        header = builder.header;

    }

    /**
     * @return
     * @throws IOException
     */
    public Content sendData() throws IOException {
        if(method == null || url ==null){
            throw new RuntimeException("必要的参数缺失");
        }
        switch (method) {
            case POST: {
                return Request.Post(url)
                        .setHeaders(header == null ? null :  (header.entrySet().stream().map(entry -> new BasicHeader(entry.getKey(),entry.getValue())).toArray(Header[]::new)))
                        .body(new StringEntity((param instanceof String ? param.toString() : JSON.toJSONString(param)), UTF_8)).execute().returnContent();
            }
            case GET: {
                return Request.Get(url)
                        .setHeaders(header == null ? null : (header.entrySet().stream().map(entry -> new BasicHeader(entry.getKey(),entry.getValue())).toArray(Header[]::new)))
                        .execute().returnContent();
            }
            case DELETE: {
                return Request.Delete(url)
                        .setHeaders(header == null ? null : header.entrySet().stream().map(entry -> new BasicHeader(entry.getKey(),entry.getValue())).toArray(Header[]::new))
                        .body(new StringEntity((param instanceof String ? param.toString() : JSON.toJSONString(param)),UTF_8)).execute().returnContent();
            }
            case PUT :{
                return Request.Put(url)
                        .setHeaders(header == null ? null : header.entrySet().stream().map(entry -> new BasicHeader(entry.getKey(),entry.getValue())).toArray(Header[]::new))
                        .body(new StringEntity((param instanceof String ? param.toString() : JSON.toJSONString(param)),UTF_8)).execute().returnContent();
            }
            default: {
                throw new RuntimeException("参数缺失");
            }
        }
    }

}