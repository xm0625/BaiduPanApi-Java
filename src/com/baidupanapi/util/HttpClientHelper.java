package com.baidupanapi.util;


import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.config.Registry;
import cz.msebera.android.httpclient.config.RegistryBuilder;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.socket.ConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.socket.PlainConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustStrategy;
import cz.msebera.android.httpclient.conn.ssl.X509HostnameVerifier;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.ssl.SSLContextBuilder;
import cz.msebera.android.httpclient.ssl.SSLContexts;
import cz.msebera.android.httpclient.util.EntityUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by xm on 15-11-19.
 */
public class HttpClientHelper {
    public static final String CHARSET = "utf-8";


    public static CloseableHttpResponse get(CloseableHttpClient httpClient, String url, Map<String, String> params, Map<String, String> headers) throws IOException {

        System.out.println("url:"+url);
        System.out.println("params:"+MapUtil.getEncodedUrl(params));
        System.out.println("headers:"+MapUtil.getEncodedUrl(headers));


        String urlParamString = MapUtil.getEncodedUrl(params);
        if(urlParamString.length()>0){
            if(url.contains("?")){
                url = String.format("%s&%s",url,urlParamString);
            }else{
                url = String.format("%s?%s",url,urlParamString);
            }
        }
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry:headers.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        return httpClient.execute(httpGet);
    }

    public static CloseableHttpResponse get(CloseableHttpClient httpClient, String url) throws IOException {
        return get(httpClient, url,new HashMap<>(),new HashMap<>());
    }

    public static CloseableHttpResponse post(CloseableHttpClient httpClient, String url, Map<String, String> params,Map<String,File> fileMap,Map<String, String> headers) throws IOException {

        System.out.println("url:"+url);
        System.out.println("params:"+MapUtil.getEncodedUrl(params));
        System.out.println("headers:"+MapUtil.getEncodedUrl(headers));
        System.out.println("data null?:"+(fileMap==null));

        if(params == null){
            params = new HashMap<>();
        }
        HttpPost httpost = new HttpPost(url);
        for(Map.Entry<String,String> entry:headers.entrySet()){
            httpost.setHeader(entry.getKey(),entry.getValue());
        }
        if(fileMap == null) {
            List<NameValuePair> nvps = new ArrayList<>();
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        }else{
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
                builder.addPart(key, new StringBody(params.get(key), contentType));
            }
            keySet = fileMap.keySet();
            for (String key : keySet) {
                builder.addPart(key,new FileBody(fileMap.get(key)));
            }
            httpost.setEntity(builder.build());
        }

        return httpClient.execute(httpost);
    }


    public static CloseableHttpResponse post(CloseableHttpClient httpClient, String url, Map<String, String> params,Map<String, String> headers) throws IOException {
        return post(httpClient, url, params, null,headers);
    }


        public static CloseableHttpResponse post(CloseableHttpClient httpClient, String url,Map<String, String> params) throws IOException {
        return post(httpClient, url, params, new HashMap<>());
    }

    public static String getResponseString(CloseableHttpResponse response,String charset) throws IOException {
        if(charset == null){
            charset = CHARSET;
        }
        return EntityUtils.toString(response.getEntity(), charset);
    }

    public static String getResponseString(CloseableHttpResponse response) throws IOException {
        return getResponseString(response, null);
    }


    public static void dumpCookies(CookieStore cookieStore){
        System.out.println("dump");
        System.out.println(cookieStore.getCookies());
        System.out.println("dump end");
    }


    public static PoolingHttpClientConnectionManager getSSLNoCheckConnectionManager() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                return true;
            }
        });
        SSLContext sslContext = builder.build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl)
                    throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert)
                    throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns,
                               String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return cm;
    }

}


