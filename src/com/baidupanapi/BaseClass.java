package com.baidupanapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidupanapi.util.HttpClientHelper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by xm on 15-11-19.
 */
public class BaseClass {

    private HttpClient session;
    private String apiTemplate = BaseData.apiTemplate;
    private BaseRunnable captchaRunnable;

    BaseClass(String username,String password,String apiTemplate,BaseRunnable captchaRunnable) throws IOException {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build();
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookiePolicy.BROWSER_COMPATIBILITY)
                .build();
        session = new DefaultHttpClient();
        session.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

        if(captchaRunnable != null){
            this.captchaRunnable = captchaRunnable;
        }else{
            this.captchaRunnable = new ShowCaptchaRunnable();
        }

        //设置pcs服务器
        System.out.println(getFastestPcsServer());
    }

    /**
     * 通过百度返回设置最快的pcs服务器
     * */
    public String getFastestPcsServer() throws IOException {
        String result = HttpClientHelper.get(session,BaseData.GET_FASTEST_PCS_SERVER_API);
        JSONObject jsonObject = (JSONObject) JSON.parse(result);
        return jsonObject.getString("host");
    }

    /**
     * 手动设置百度pcs服务器
     * @params server: 服务器地址或域名
     * warning:: 不要加 http:// 和末尾的 /
     * */
    public void setPcsServer(String server){
        BaseData.BAIDUPCS_SERVER = server;
    }


    class ShowCaptchaRunnable extends BaseRunnable {

        @Override
        List<Object> execute(Object... paramList) throws Exception{
            //传入一个参数 url_verify_code
            List<Object> resultList;
            String urlVerifyCode = String.valueOf(paramList[0]);
            InputStreamReader isr = new InputStreamReader(System.in);

            BufferedReader br = new BufferedReader(isr);
            String verifyCode = br.readLine();
            resultList = new ArrayList<>();
            resultList.add(verifyCode);
            return resultList;
        }
    }


    abstract class BaseRunnable {
        abstract List<Object> execute(Object... paramList) throws Exception;
    }
}
