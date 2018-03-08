package com.nix.client.common;


/**
 * Created by 11723 on 2017/1/20.
 */
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * @author 11723
 */
public class HttpsClient {
    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static String doGet(String url, Map<String,String> keyValueParams){
        String result = "";
        BufferedReader in = null;
        try {
            String urlStr = url + getParamStr(keyValueParams);
            System.out.println("GET请求的URL为："+urlStr);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                    new java.security.SecureRandom());
            URL realUrl = new URL(urlStr);
            // 打开和URL之间的连接
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            //设置https相关属性
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setDoOutput(true);

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println("获取的结果为："+result);
        } catch (Exception e) {
            return null;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                return null;
            }
        }
        return result;

    }

    public static String doPost(String strURL, Map<String,String> params){
        try {
            // 创建连接
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置接收数据的格式
            connection.setRequestProperty("Accept", "application/json");
            // 设置发送数据的格式
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            // utf-8编码
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.append(params.toString());
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            // 获取长度
            int length = (int) connection.getContentLength();
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                // utf-8编码
                String result = new String(data, "UTF-8");
                return result;
            }

        } catch (IOException e) {

        }
        // 自定义错误信息
        return "error";

    }

    private static String getParamStr(Map<String, String> params)
    {
        if (params == null || params.size() == 0) {
            return null;
        }
        String paramStr="?";
        for (String key : params.keySet()) {
            paramStr += key+"="+params.get(key)+"&";
        }
        //去除最后一个"&"
        paramStr=paramStr.substring(0, paramStr.length()-1);
        return paramStr;
    }
}
