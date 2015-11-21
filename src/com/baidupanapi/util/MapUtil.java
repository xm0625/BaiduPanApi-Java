package com.baidupanapi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xm on 15/11/21.
 */
public class MapUtil {
    public static final String CHARSET = "utf-8";

    public static void removeNullPair(Map map){
        List<Object> nullPairKeyList = new ArrayList<>();
        for(Object key:map.keySet()){
            if(map.get(key) == null){
                nullPairKeyList.add(key);
            }
        }
        for(Object nullPairKey:nullPairKeyList){
            map.remove(nullPairKey);
        }
    }

    public static void updateMap(Map oldOne,Map newOne){
        for(Object key:newOne.keySet()){
            oldOne.put(key,newOne.get(key));
        }
    }


    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getEncodedUrl(Map map) throws UnsupportedEncodingException {
        if (map == null || map.size()==0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object entryKey : map.keySet()) {
            if(map.get(entryKey) == null){
                map.put(entryKey,"");
            }
            sb.append(entryKey + "=" + URLEncoder.encode(String.valueOf(map.get(entryKey)), CHARSET));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
