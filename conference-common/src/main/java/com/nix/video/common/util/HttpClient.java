package com.nix.video.common.util;


/**
 * Created by 11723 on 2017/1/20.
 */
import com.nix.video.common.util.log.LogKit;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * @author 11723
 */
public class HttpClient {
    public enum HttpMethod{
        POST,
        PUT,
        DELETE,
        GET
    }
    public static String doHttp(String url,HttpMethod method , Map<String,String> param){
        try {
            return new HttpConnect.Builder()
                    .jsonData(null)
                    .url(url)
                    .method(method.name())
                    .build().sendData().result();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
