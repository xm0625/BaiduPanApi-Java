package com.baidupanapi;

import com.baidupanapi.runnable.base.BaseRunnable;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by xm on 15/11/21.
 */
public class BaiduPanService extends BaseClass{
    BaiduPanService(String username, String password, BaseRunnable captchaRunnable) throws Exception {
        super(username, password, BaseData.apiTemplate, captchaRunnable);
    }

    /**
     * 获得配额信息
     * @return 返回请求返回的字符串
     *
     * 返回正确时返回字符串中的数据结构
     * {"errno":0,"total":配额字节数,"used":已使用字节数,"request_id":请求识别号}
     * */
    public String quota(Map<String,Object> keyValueArgs) throws IOException {
        return request("quota",null,null,null,null,null,null,keyValueArgs);
    }

    /**
     * 获取目录下的文件列表
     *
     *
     * */
    public String listFiles(String remotePath,String orderBy,String orderType,Integer startIndex,Integer endIndex, Map<String,Object> keyValueArgs){
        //设置默认值

    }
}
