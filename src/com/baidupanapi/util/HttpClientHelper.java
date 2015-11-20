package com.baidupanapi.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xm on 15-11-19.
 */
public class HttpClientHelper {
    public static final String COOKIES_FILE_NAME = "cookies.conf";

    public static String get(CloseableHttpClient httpClient, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response=httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), "utf-8");
    }
    public static String post(CloseableHttpClient httpClient, String url,Map<String, String> params) throws IOException {

        HttpPost httpost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        CloseableHttpResponse response=httpClient.execute(httpost);
        return EntityUtils.toString(response.getEntity(), "utf-8");
    }

    public static void dumpCookies(CookieStore cookieStore){
        System.out.println("dump");
        System.out.println(cookieStore.getCookies());
        System.out.println("dump end");
    }

}
