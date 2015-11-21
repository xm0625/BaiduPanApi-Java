package com.baidupanapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidupanapi.exception.LoginFailedException;
import com.baidupanapi.util.*;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by xm on 15-11-19.
 */
public class BaseClass {

    private CloseableHttpClient session;
    private CookieStore cookieStore = new BasicCookieStore();

    private String apiTemplate = BaseData.apiTemplate;
    private String username;
    private String password;
    private Map<String,String> user = new HashMap<>();

    private BaseRunnable captchaRunnable;
    private BaseRunnable progressRunnable;

    BaseClass(String username,String password,String apiTemplate,BaseRunnable captchaRunnable) throws Exception {
        try {
            session = HttpClients.custom().useSystemProperties()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }catch (NoClassDefFoundError e){
            session = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }
        this.username = username;
        this.password = password;
        if(captchaRunnable != null){
            this.captchaRunnable = captchaRunnable;
        }else{
            this.captchaRunnable = new ShowCaptchaRunnable();
        }

        System.out.println("设置pcs服务器");
        setPcsServer(getFastestPcsServer());

        initiate();
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

    private void initiate() throws Exception {
        if(!loadCookies()){
            HttpClientHelper.get(session,BaseData.COOKIES_INIT_API);
            user.put("token",getToken());
            login();
        }else{
            user.put("token",getToken());
        }
    }

    private boolean loadCookies(){
        System.out.println("加载已存在的Cookie");

        //没有已存在的cookie则返回false
        return false;
    }

    private void saveCookies(){
        System.out.println("保存Cookie");
    }

    private String getToken() throws IOException {
        String result = HttpClientHelper.get(session,BaseData.getTokenApi());
        JSONObject jsonObject = (JSONObject) JSON.parse(result);
        return jsonObject.getJSONObject("data").getString("token");
    }

    private void login() throws Exception {
        String captcha = "";
        String codeString = "";
        String content = "";
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
            content = HttpClientHelper.post(session,BaseData.LOGIN_API,loginData);
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

    private void checkAccountException(String content){
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

    private String getCaptcha(String codeString) throws Exception {
        String verifyCode = "";
        if(codeString != null) {
            verifyCode = (String) captchaRunnable.execute(BaseData.GET_VERIFY_CODE_API+codeString).get(0);
        }
        return verifyCode;
    }

    private PublicKeyEntity getPublickey() throws IOException {
        String url = BaseData.getPublicKeyApi(user.get("token"));
        String content = HttpClientHelper.get(session,url);
        JSONObject jsonObject = (JSONObject) JSON.parse(content);
        return new PublicKeyEntity(jsonObject.getString("pubkey"),jsonObject.getString("key"));
    }

    class ShowCaptchaRunnable extends BaseRunnable {

        @Override
        List<Object> execute(Object... paramList) throws Exception{
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


    abstract class BaseRunnable {
        abstract List<Object> execute(Object... paramList) throws Exception;
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
