package com.baidupanapi.util;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by xm on 15-11-20.
 */
public class CookieStoreHelper {

    public static String get(CookieStore cookieStore, String key){
        List<Cookie> cookieList  = cookieStore.getCookies();
        for(Cookie cookie:cookieList){
            if(cookie.getName().equals(key)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
