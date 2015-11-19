package com.baidupanapi;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseData {

    /** 所有API URL **/
    /** 获取最快的pcs服务器 **/
    public static final String GET_FASTEST_PCS_SERVER_API = "http://pcs.baidu.com/rest/2.0/pcs/file?app_id=250528&method=locateupload";

    public static final String BAIDUPAN_SERVER = "pan.baidu.com";
    public static String BAIDUPCS_SERVER = "pcs.baidu.com";

    public static Map<String,Object> baidupanHeaders;
    public static String apiTemplate;

    static{
        baidupanHeaders = new HashMap<String, Object>();
        baidupanHeaders.put("Referer", "http://pan.baidu.com/disk/home");
        baidupanHeaders.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");

        apiTemplate = String.format("http://%s/api/{0}",BAIDUPAN_SERVER).toString();
    }

    public static void main(String[] args) throws IOException {
	// write your code here
        BaseClass baseClass = new BaseClass("402276694","19930625",null,null);
    }

}
