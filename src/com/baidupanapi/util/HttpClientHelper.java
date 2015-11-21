package com.baidupanapi.util;


import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.*;

/**
 * Created by xm on 15-11-19.
 */
public class HttpClientHelper {
    public static final String CHARSET = "utf-8";


    public static CloseableHttpResponse get(CloseableHttpClient httpClient, String url, Map<String, String> params, Map<String, String> headers) throws IOException {
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

    public static CloseableHttpResponse post(CloseableHttpClient httpClient, String url, Map<String, String> params, Map<String, String> headers) throws IOException {

        HttpPost httpost = new HttpPost(url);
        for(Map.Entry<String,String> entry:headers.entrySet()){
            httpost.setHeader(entry.getKey(),entry.getValue());
        }
        List<NameValuePair> nvps = new ArrayList<>();

        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        return httpClient.execute(httpost);
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

}
