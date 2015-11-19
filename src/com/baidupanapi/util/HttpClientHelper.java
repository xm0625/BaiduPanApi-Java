package com.baidupanapi.util;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by xm on 15-11-19.
 */
public class HttpClientHelper {


    public static String get(HttpClient httpClient, String url) throws IOException {
        HttpGet httpGet=new HttpGet(url);
        HttpResponse response=httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), "utf-8");
    }

    public static void dumpCookies(HttpClient httpClient){
        CookieSpec cookiespec = CookiePolicy;
        Cookie[] cookies = cookiespec.match("域名", 80/*端口*/, "/" , false , client.getState().getCookies());
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName() + "##" + cookie.getValue());
        }
    }
}
