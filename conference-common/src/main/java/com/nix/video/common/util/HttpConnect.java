package com.nix.video.common.util;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


/**
 * @author keray
 * @date 2018/10/27 14:11
 */
public class HttpConnect {
    public enum HttpMethod{
        POST,
        PUT,
        DELETE,
        GET
    }
    public static String doHttp(String url,HttpConnect.HttpMethod method , Map<String,String> param){
        try {
            return new HttpConnect.Builder()
                    .jsonData(null)
                    .url(url)
                    .method(method)
                    .build().sendData().result();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String jsonData;
    private Object object;
    private String url;
    private HttpMethod method;
    private Map<String, String> header;
    private String[] filepath;
    private String[] filename;


    public static class Builder {
        private String jsonData;
        private Object object;
        private String url;
        private HttpMethod method;
        private Map<String, String> header;
        private String[] filepath;
        private String[] filename;




        private Builder filename(String[] val){
            filename=  val;
            return this;
        }

        private Builder filepath(String[] val){
            filepath=  val;
            return this;
        }


        public Builder jsonData(String val) {
            jsonData = val;
            return this;
        }

        public Builder object(Object val) {
            object = val;
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

        jsonData = builder.jsonData;
        object = builder.object;
        url = builder.url;
        method  = builder.method;
        header = builder.header;
        filepath = builder.filepath;
        filename = builder.filename;

    }
    public class Response {
        int responseCode;
        String responseData;

        private Response(int responseCode, String responseData) {
            this.responseCode = responseCode;
            this.responseData = responseData;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public Object getResponseData() {
            return responseData;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
        }

        public String result() {
            if (responseCode < 300) {
                return responseData;
            }
            return null;
        }
    }


    Gson gson = new Gson();

    /**
     * @return
     * @throws IOException
     */
    public Response sendData() throws IOException, StatementException {
        if(method == null || url ==null){
            throw new StatementException("必要的参数缺失");
        }
        switch (method) {
            case POST: {
                HttpClient httpClient = getHttpClient();

                HttpPost httpPost = new HttpPost(url);
                if (header != null) {
                    for (Map.Entry<String, String> entry : header.entrySet()) {
                        httpPost.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                return setEntity(httpClient,httpPost);
            }
            case GET: {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                if (header != null) {
                    for (Map.Entry<String, String> entry : header.entrySet()) {
                        httpGet.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                HttpResponse response = httpClient.execute(httpGet);
                String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                return new Response(response.getStatusLine().getStatusCode(),result);
            }
            case DELETE: {
                HttpClient httpClient = new DefaultHttpClient();
                HttpDelete httpDelete = new HttpDelete(url);
                if (header != null) {
                    for (Map.Entry<String, String> entry : header.entrySet()) {
                        httpDelete.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                HttpResponse response = httpClient.execute(httpDelete);
                String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                return new Response(response.getStatusLine().getStatusCode(),result);
            }
            case PUT :{
                HttpClient httpClient = getHttpClient();

                HttpPut httpPut = new HttpPut(url);
                if (header != null) {
                    for (Map.Entry<String, String> entry : header.entrySet()) {
                        httpPut.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                return setEntity(httpClient,httpPut);
            }
            default: {
                throw new StatementException("参数缺失");
            }
        }
    }

    private Response setEntity(HttpClient httpClient, HttpEntityEnclosingRequestBase httpMethod) throws IOException {

        if (object != null && jsonData == null) {
            StringEntity entity = new StringEntity(gson.toJson(object), "UTF-8");//设置StringEntity编码为utf-8
            httpMethod.setEntity(entity);
            HttpResponse response = httpClient.execute(httpMethod);

            String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            return new Response(response.getStatusLine().getStatusCode(),result);

        } else if (jsonData != null && object == null) {
            StringEntity entity = new StringEntity(jsonData, "UTF-8");//设置StringEntity编码为utf-8
            httpMethod.setEntity(entity);
            HttpResponse response = httpClient.execute(httpMethod);
            String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            return new Response(response.getStatusLine().getStatusCode(),result);
        } else {
            HttpResponse response = httpClient.execute(httpMethod);
            String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            return new Response(response.getStatusLine().getStatusCode(),result);
        }
    }

    /**
     * 发送资源
     * @return
     * @throws StatementException
     */
    public Map<String,Object> sendResource() throws StatementException {

        Map<String,Object> map = new HashMap<>();
        HttpClient httpClient = new DefaultHttpClient();

        try {

            HttpPost httpPost = new HttpPost(url);

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            FileBody[] bins = new FileBody[filepath.length];
            MultipartEntity reqEntity = new MultipartEntity();
            for(int i = 0 ;i<filepath.length;i++){
                bins[i] = new FileBody(new File(filepath[i]));
                reqEntity.addPart(filename[i],bins[i]);
            }
            httpPost.setEntity(reqEntity);
            HttpResponse responses = httpClient.execute(httpPost);
            String result = EntityUtils.toString(responses.getEntity(), Charset.forName("UTF-8"));

            map.put("response",responses);
            map.put("result",result);
            return map;


        } catch (IOException e) {
            e.printStackTrace();
            throw new StatementException("參數缺失");
        }

    }

    /**
     * 获取资源
     * @return
     * @throws IOException
     */
    public Map<String,Object>getResource() throws IOException {

        Map<String,Object> map = new HashMap<>();

        HttpGet httpGet = new HttpGet(url);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        URL urls = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();//通过输入流获取图片数据

        conn.getContentType();
        map.put("type",conn.getContentType());
        map.put("stream",inStream);
        return map;

    }



    private static DefaultHttpClient getHttpClient() {
        try {
            // 禁止https证书验证
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//运行所有的hostname验证

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            // 禁用Cookie2请求头
            HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
            HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
            HttpClientParams.setCookiePolicy(params, CookiePolicy.NETSCAPE);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);

            return new DefaultHttpClient(httpParams);
        }
    }



    static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }



        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    public class StatementException extends  Exception{

        public StatementException(String msg)
        {
            super(msg);
        }
    }
}