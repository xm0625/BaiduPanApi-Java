package com.baidupanapi.util;



import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

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
