package com.baidupanapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidupanapi.exception.base.LoginFailedException;
import com.baidupanapi.runnable.base.BaseRunnable;
import com.baidupanapi.util.*;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by xm on 15-11-19.
 */
public class BaseClass {

    protected CloseableHttpClient session;
    protected CookieStore cookieStore = new BasicCookieStore();

    protected String apiTemplate;
    protected String username;
    protected String password;
    protected Map<String,String> user = new HashMap<>();

    protected BaseRunnable captchaRunnable;
    protected BaseRunnable progressRunnable;

    BaseClass(String username,String password,String apiTemplate,BaseRunnable captchaRunnable) throws Exception {
        if(apiTemplate==null){
            apiTemplate = BaseData.apiTemplate;
        }

        try {
            session = HttpClients.custom().useSystemProperties()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }catch (NoClassDefFoundError e){
            session = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }
        this.apiTemplate = apiTemplate;
        this.username = username;
        this.password = password;
        if(captchaRunnable != null){
            this.captchaRunnable = captchaRunnable;
        }else{
            this.captchaRunnable = new ShowCaptchaRunnable();
        }
        this.progressRunnable = null;

        System.out.println("设置pcs服务器");
        setPcsServer(getFastestPcsServer());

        initiate();
    }

    /**
     * 通过百度返回设置最快的pcs服务器
     * */
    public String getFastestPcsServer() throws IOException {
        String content = HttpClientHelper.getResponseString(HttpClientHelper.get(session, BaseData.GET_FASTEST_PCS_SERVER_API));
        JSONObject jsonObject = (JSONObject) JSON.parse(content);
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

    protected void initiate() throws Exception {
        if(!loadCookies()){
            HttpClientHelper.get(session,BaseData.COOKIES_INIT_API);
            user.put("token",getToken());
            login();
        }else{
            user.put("token",getToken());
        }
    }

    protected boolean loadCookies(){
        System.out.println("加载已存在的Cookie");

        //没有已存在的cookie则返回false
        return false;
    }

    protected void saveCookies(){
        System.out.println("保存Cookie");
    }

    protected void clearCookies(){
        System.out.println("清理Cookie");
    }

    protected String getToken() throws IOException {
        String content = HttpClientHelper.getResponseString(HttpClientHelper.get(session, BaseData.getTokenApi()));
        JSONObject jsonObject = (JSONObject) JSON.parse(content);
        return jsonObject.getJSONObject("data").getString("token");
    }

    protected void login() throws Exception {
        String captcha = "";
        String codeString = "";
        String content;
        PublicKeyEntity publicKeyEntity = getPublickey();
        String passwordEncrypted = RSAUtils.encryptToBase64StringByPublicKey(RSAUtils.readPublicKey(publicKeyEntity.getPublicKey()),password);
        while (true){
            Map<String,String> loginData = new HashMap<>();
            loginData.put("staticpage","http://www.baidu.com/cache/user/html/v3Jump.html");
            loginData.put("charset", "UTF-8");
            loginData.put("token", user.get("token"));
            loginData.put("tpl", "pp");
            loginData.put("subpro", "");
            loginData.put("apiver", "v3");
            loginData.put("tt", TimeUtil.getSecondTime());
            loginData.put("codestring", codeString);
            loginData.put("isPhone", "false");
            loginData.put("safeflg", "0");
            loginData.put("u", "https://passport.baidu.com/");
            loginData.put("quick_user", "0");
            loginData.put("logLoginType", "pc_loginBasic");
            loginData.put("loginmerge", "true");
            loginData.put("logintype", "basicLogin");
            loginData.put("username", username);
            loginData.put("password", passwordEncrypted);
            loginData.put("verifycode", captcha);
            loginData.put("mem_pass", "on");
            loginData.put("rsakey", publicKeyEntity.getRsaKey());
            loginData.put("crypttype", "12");
            loginData.put("ppui_logintime", "50918");
            loginData.put("callback", "parent.bd__pcbs__oa36qm");
            content = HttpClientHelper.getResponseString(HttpClientHelper.post(session, BaseData.LOGIN_API, loginData));
            if(content.contains("err_no=257") || content.contains("err_no=6")){
                System.out.println("需要验证码");
                codeString = RegexUtil.findAll(content,"codeString=(.*?)&",1).get(0);
                System.out.println("need captcha, codeString="+codeString);
                captcha = getCaptcha(codeString);
                continue;
            }
            break;
        }

        //异常检查
        checkAccountException(content);

        user.put("BDUSS", CookieStoreHelper.get(cookieStore,"BDUSS"));

        saveCookies();
    }

    protected void checkAccountException(String content){
        String errorId = RegexUtil.findAll(content, "err_no=([\\d]+)",1).get(0);
        if(errorId.equals("0")){
            return;
        }

        String msg;
        if(BaseData.errorMessageMap.containsKey(errorId)){
            msg = BaseData.errorMessageMap.get(errorId);
        }else{
            msg = "unknown err_id=" + errorId;
        }
        throw new LoginFailedException(msg);
    }

    protected String getCaptcha(String codeString) throws Exception {
        String verifyCode = "";
        if(codeString != null) {
            verifyCode = (String) captchaRunnable.execute(BaseData.GET_VERIFY_CODE_API+codeString).get(0);
        }
        return verifyCode;
    }

    protected PublicKeyEntity getPublickey() throws IOException {
        String url = BaseData.getPublicKeyApi(user.get("token"));
        String content = HttpClientHelper.getResponseString(HttpClientHelper.get(session, url));
        JSONObject jsonObject = (JSONObject) JSON.parse(content);
        return new PublicKeyEntity(jsonObject.getString("pubkey"),jsonObject.getString("key"));
    }

    protected String checkLogin(CloseableHttpResponse response) throws IOException {
        String content = HttpClientHelper.getResponseString(response);
        try {
            JSONObject jsonObject = (JSONObject) JSON.parse(content);
            if(jsonObject.containsKey("errno") && String.valueOf(jsonObject.get("errno")).equals("-6")){
                clearCookies();
                initiate();
            }else{
                return content;
            }
        }catch (Exception e){
        }
        return null;

    }



    protected String request(String uri,String method, String url, Map<String,String> extraParams, Map<String,String> data, Map<String,File> files, BaseRunnable callback, Map<String,Object> keyValueArgs) throws IOException {
        if(keyValueArgs == null){
            keyValueArgs = new HashMap<>();
        }

        CloseableHttpResponse response;
        String api;

        Map<String,String> params = new HashMap<>();
        params.put("","");
        params.put("method",method);
        params.put("app_id","250528");
        params.put("BDUSS",user.get("BDUSS"));
        params.put("t",TimeUtil.getSecondTime());
        params.put("bdstoken",user.get("token"));

        if(extraParams!=null){
            MapUtil.updateMap(params,extraParams);
            MapUtil.removeNullPair(params);
        }

        Map<String,String> headers = new HashMap<>();
        headers.putAll(BaseData.baidupanHeaders);
        if(keyValueArgs.containsKey("headers")){
            MapUtil.updateMap(headers, (Map) keyValueArgs.get("headers"));
            keyValueArgs.remove("headers");
        }

        if(url==null){
            url = String.format(apiTemplate,uri);
        }

        if(data!=null || files!=null){
            if(url.contains("?")){
                api = String.format("%s&%s",url, MapUtil.getEncodedUrl(params));
            }else{
                api = String.format("%s?%s",url, MapUtil.getEncodedUrl(params));
            }

            if(data!=null){
                MapUtil.removeNullPair(data);
                response = HttpClientHelper.post(session,api,data,headers);
            }else{
                MapUtil.removeNullPair(files);
                throw new RuntimeException("File Upload Feature Not Done!!!");
            }
        }else{
            api = url;
            if(uri.equals("filemanager") || uri.equals("rapidupload") || uri.equals("filemetas") || uri.equals("precreate")){
                response = HttpClientHelper.post(session,api,params,headers);
            }else{
                response = HttpClientHelper.post(session,api,params,headers);
            }
        }
        return checkLogin(response);
    }

    class ShowCaptchaRunnable extends BaseRunnable {

        @Override
        public List<Object> execute(Object... paramList) throws Exception{
            //传入一个参数 url_verify_code
            List<Object> resultList;
            String urlVerifyCode = String.valueOf(paramList[0]);
            System.out.println(urlVerifyCode);
            System.out.println("open url aboved with your web browser, then input verify code > ");
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String verifyCode = br.readLine();
            resultList = new ArrayList<>();
            resultList.add(verifyCode);
            return resultList;
        }
    }


    class PublicKeyEntity{
        private String publicKey;
        private String rsaKey;

        public PublicKeyEntity(String publicKey, String rsaKey) {
            this.publicKey = publicKey;
            this.rsaKey = rsaKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getRsaKey() {
            return rsaKey;
        }

        public void setRsaKey(String rsaKey) {
            this.rsaKey = rsaKey;
        }
    }

}
