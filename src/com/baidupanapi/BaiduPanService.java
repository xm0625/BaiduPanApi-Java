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


    public String quota(Map<String,Object> keyValueArgs) throws IOException {
        return request("quota",null,null,null,null,null,null,keyValueArgs);
    }
}
